/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seanproctor.datatable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.*
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Layout model that arranges its children into rows and columns.
 */
@Composable
fun Table(
    columns: List<TableColumnDefinition>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { Divider(Modifier.height(1.dp)) },
    content: TableScope.() -> Unit
) {

    val tableContent: @Composable () -> Unit = with(TableScopeImpl()) {
        apply(content); @Composable {

        columns.forEachIndexed { columnIndex, columnDefinition ->
            with(TableCellScopeImpl(0, columnIndex)) {
                val cellScope = this
                Box(
                    Modifier.tableCell()
                        .padding(horizontal = 16.dp)
                        .heightIn(min = 56.dp),
                    contentAlignment = columnDefinition.alignment
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.subtitle2
                    ) {
                        cellScope.(columnDefinition.header)()
                    }
                }
            }
        }

        tableRows.forEachIndexed { rowIndex, rowFunction ->
            with(TableRowScopeImpl(rowIndex + 1)) {
                rowFunction()
                cells.forEachIndexed { columnIndex, cellFunction ->
                    with(TableCellScopeImpl(rowIndex + 1, columnIndex)) {
                        val cellScope = this
                        Box(
                            Modifier.tableCell()
                                .padding(horizontal = 16.dp)
                                .heightIn(min = 52.dp),
                            contentAlignment = columns[columnIndex].alignment
                        ) {
                            CompositionLocalProvider(
                                LocalTextStyle provides MaterialTheme.typography.body2
                            ) {
                                cellScope.cellFunction()
                            }
                        }
                    }
                }
            }
        }
    }
    }

    val separators = @Composable {
        for (column in columns.indices) {
            separator(column)
        }
    }

    val layoutDirection = LocalLayoutDirection.current
    val columnCount = columns.size
    Layout(
        listOf(tableContent, separators),
        modifier,
    ) { (contentMeasurables, separatorMeasurables), constraints ->
        val rowMeasurables = contentMeasurables.groupBy { it.rowIndex }
        val rowCount = rowMeasurables.size
        println("Rows: $rowCount")
        fun measurableAt(row: Int, column: Int) = rowMeasurables[row]?.getOrNull(column)
        val placeables = Array(rowCount) { arrayOfNulls<Placeable>(columnCount) }

        // Compute column widths and collect flex information.
        var totalFlex = 0f
        println("Max width: ${constraints.maxWidth}")
        val columnWidths = Array(columnCount) { 0 }
        var minTableWidth = 0
        var neededColumnWidth = 0
        for (column in 0 until columnCount) {
            val spec = columns[column].width
            val cells = List(rowCount) { row ->
                TableMeasurable(
                    preferredWidth = {
                        placeables[row][column]?.width
                            ?: measurableAt(row, column)?.measure(Constraints())
                                ?.also { placeables[row][column] = it }?.width ?: 0
                    },
                    minIntrinsicWidth = {
                        measurableAt(row, column)?.minIntrinsicWidth(it) ?: 0
                    },
                    maxIntrinsicWidth = {
                        measurableAt(row, column)?.maxIntrinsicWidth(it) ?: 0
                    }
                )
            }
            minTableWidth += spec.minIntrinsicWidth(cells, constraints.maxWidth, this, constraints.maxHeight)
            columnWidths[column] = spec.preferredWidth(cells, constraints.maxWidth, this)
            neededColumnWidth += columnWidths[column]
            totalFlex += spec.flexValue
        }
        val availableSpace =
            if (constraints.maxWidth == Constraints.Infinity) constraints.maxWidth else max(
                constraints.minWidth,
                minTableWidth
            )
        val remainingSpace = availableSpace - neededColumnWidth

        // Grow flexible columns to fill available horizontal space.
        println("total flex: $totalFlex, available space: $availableSpace")
        if (totalFlex > 0 && remainingSpace > 0) {
            for (column in 0 until columnCount) {
                val spec = columns[column].width
                if (spec.flexValue > 0) {
                    columnWidths[column] += (remainingSpace * (spec.flexValue / totalFlex)).roundToInt()
                }
            }
        }

        // Measure the remaining children and calculate row heights.
        val rowHeights = Array(rowCount) { 0 }
        for (row in 0 until rowCount) {
            for (column in 0 until columnCount) {
                if (placeables[row][column] == null) {
                    placeables[row][column] = measurableAt(row, column)?.measure(
                        Constraints(minWidth = 0, maxWidth = columnWidths[column])
                    )
                }
                rowHeights[row] =
                    max(rowHeights[row], placeables[row][column]?.height ?: 0)
            }
        }

        val columnOffsets = Array(columnCount + 1) { 0 }
        for (column in 0 until columnCount) {
            columnOffsets[column + 1] = columnOffsets[column] + columnWidths[column]
        }

        val separatorPlaceables = separatorMeasurables.mapIndexed { index, measurable ->
            val separatorPlaceable = measurable.measure(constraints.copy(maxWidth = columnOffsets[columnCount]))
            rowHeights[index] += separatorPlaceable.height
            separatorPlaceable
        }

        println("Column widths: ${columnWidths.toList()}")
        println("Row heights: ${rowHeights.toList()}")

        // Compute row/column offsets.
        val rowOffsets = Array(rowCount + 1) { 0 }

        for (row in 0 until rowCount) {
            rowOffsets[row + 1] = rowOffsets[row] + rowHeights[row]
        }
        println("Column offsets: ${columnOffsets.toList()}")

        // TODO(calintat): Do something when these do not satisfy constraints.
        val tableSize = constraints.constrain(IntSize(columnOffsets[columnCount], rowOffsets[rowCount]))

        layout(tableSize.width, tableSize.height) {
            for (row in 0 until rowCount) {
                for (column in 0 until columnCount) {
                    placeables[row][column]?.let {
                        val position = columns[column].alignment.align(
                            IntSize(it.width, it.height),
                            IntSize(
                                width = columnWidths[column],
                                height = rowHeights[row]
                            ),
                            layoutDirection
                        )
                        println("Placing at: ${columnOffsets[column]} + ${position.x}, ${rowOffsets[row]} + ${position.y}")
                        it.place(
                            x = columnOffsets[column] + position.x,
                            y = rowOffsets[row] + position.y
                        )
                    }
                }
                separatorPlaceables[row].place(x = 0, y = rowOffsets[row])
            }
        }
    }
}
