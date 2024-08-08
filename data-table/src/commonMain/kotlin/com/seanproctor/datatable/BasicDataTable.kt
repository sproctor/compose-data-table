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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun BasicDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    state: DataTableState = rememberDataTableState(),
    separator: @Composable (rowIndex: Int) -> Unit = { },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    background: Color = Color.Unspecified,
    footer: @Composable () -> Unit = { },
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    content: DataTableScope.() -> Unit
) {
    val tableScope = DataTableScopeImpl()
    val tableContent: @Composable () -> Unit = with(tableScope) {
        apply(content); @Composable {

        columns.forEachIndexed { columnIndex, columnDefinition ->
            with(TableCellScopeImpl(0, columnIndex)) {
                val cellScope = this
                Row(
                    Modifier.tableCell()
                        .padding(horizontal = horizontalPadding)
                        .height(headerHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sorted = columnIndex == sortColumnIndex
                    cellContentProvider.HeaderCellContent(
                        sorted = sorted,
                        sortAscending = sortAscending,
                        onClick = columnDefinition.onSort?.let {
                            { it(columnIndex, if (sorted) !sortAscending else sortAscending) }
                        }
                    ) {
                        cellScope.(columnDefinition.header)()
                    }
                }
            }
        }

        tableRows.forEachIndexed { rowIndex, rowData ->
            with(TableRowScopeImpl(rowIndex + 1)) {
                rowData.content(this)
                check(cells.size <= columns.size) { "Row ${this.rowIndex} has too many cells." }
                check(cells.size >= columns.size) { "Row ${this.rowIndex} doesn't have enough cells." }
                cells.forEachIndexed { columnIndex, cellData ->
                    with(TableCellScopeImpl(rowIndex + 1, columnIndex)) {
                        val cellScope = this
                        Row(
                            Modifier.tableCell()
                                .padding(horizontal = horizontalPadding)
                                .height(rowHeight),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            cellContentProvider.RowCellContent {
                                cellData.content(cellScope)
                            }
                        }
                    }
                }
            }
        }
    }
    }

    val separators = @Composable {
        val rows = tableScope.tableRows.size + 1 // table rows + header
        for (row in 0 until rows) {
            separator(row)
        }
    }

    val headerBackground = @Composable {
        Box(
            Modifier
                .background(background)
                .fillMaxSize()
        )
    }
    val footerBackground = @Composable {
        Box(
            Modifier
                .background(background)
                .fillMaxSize()
        )
    }
    val rowBackgrounds = @Composable {
        // Header background
        Box(
            Modifier
                .background(background)
                .fillMaxSize()
        )
        // Row backgrounds
        tableScope.tableRows.forEach {
            Box(Modifier
                .background(background)
                .fillMaxSize()
                .then(if (it.onClick != null) Modifier.clickable { it.onClick.invoke() } else Modifier)
            )
        }
        // Footer background
        Box(
            Modifier
                .background(background)
                .fillMaxSize()
        )
    }

    val layoutDirection = LocalLayoutDirection.current
    val columnCount = columns.size
    Layout(
        listOf(tableContent, separators, rowBackgrounds, footer),
        modifier
            .then(state.awaitLayoutModifier)
            .scrollable(
                state = state,
                orientation = Orientation.Vertical,
                interactionSource = state.internalInteractionSource,
            )
    ) { (contentMeasurables, separatorMeasurables, rowBackgroundMeasurables, footerMeasurable), constraints ->
        state.viewportHeight = constraints.maxHeight
        val rowMeasurables = contentMeasurables.groupBy { it.rowIndex }
        val rowCount = rowMeasurables.size
        fun measurableAt(row: Int, column: Int) = rowMeasurables[row]?.getOrNull(column)
        val placeables = Array(rowCount) { arrayOfNulls<Placeable>(columnCount) }

        // Compute column widths and collect flex information.
        var totalFlex = 0f
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
                        val height = if (row == 0) {
                            headerHeight.roundToPx()
                        } else {
                            rowHeight.roundToPx()
                        }
                        measurableAt(row, column)?.minIntrinsicWidth(height) ?: 0
                    },
                    maxIntrinsicWidth = {
                        val height = if (row == 0) {
                            headerHeight.roundToPx()
                        } else {
                            rowHeight.roundToPx()
                        }
                        measurableAt(row, column)?.maxIntrinsicWidth(height) ?: 0
                    }
                )
            }
            minTableWidth += spec.minIntrinsicWidth(
                cells,
                constraints.maxWidth,
                this,
                constraints.maxHeight
            )
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
        if (totalFlex > 0 && remainingSpace > 0) {
            for (column in 0 until columnCount) {
                val spec = columns[column].width
                if (spec.flexValue > 0) {
                    columnWidths[column] += (remainingSpace * (spec.flexValue / totalFlex)).roundToInt()
                }
            }
        }

        // Measure the remaining children and calculate row heights.
        val rowHeights = Array(rowCount + 1) { 0 }
        for (row in 0 until rowCount) {
            for (column in 0 until columnCount) {
                if (placeables[row][column] == null) {
                    placeables[row][column] = measurableAt(row, column)?.measure(
                        Constraints(minWidth = 0, maxWidth = columnWidths[column])
                    )
                }
                val cellHeight = placeables[row][column]?.height ?: 0
                rowHeights[row] = max(rowHeights[row], cellHeight)
            }
        }

        val columnOffsets = Array(columnCount + 1) { 0 }
        for (column in 0 until columnCount) {
            columnOffsets[column + 1] = columnOffsets[column] + columnWidths[column]
        }

        val footerPlaceable = footerMeasurable
            .firstOrNull()
            ?.measure(
                Constraints(
                    minWidth = max(
                        constraints.minWidth,
                        columnOffsets[columnCount]
                    )
                )
            )
            ?.also {
                rowHeights[rowCount] = it.height
            }

        val tableWidth = listOf(constraints.minWidth, columnOffsets[columnCount], footerPlaceable?.width ?: 0).max()

        val separatorPlaceables = separatorMeasurables.mapIndexed { index, measurable ->
            val separatorPlaceable = measurable.measure(Constraints(minWidth = 0, maxWidth = tableWidth))
            rowHeights[index] += separatorPlaceable.height
            separatorPlaceable
        }

        // Compute row/column offsets.
        val rowOffsets = Array(rowCount + 1) { 0 }
        for (row in 0 until rowCount) {
            rowOffsets[row + 1] = rowOffsets[row] + rowHeights[row]
        }

        val totalHeight = rowOffsets[rowCount] + (footerPlaceable?.height ?: 0)
        state.totalHeight = totalHeight
        val tableHeight = max(constraints.minHeight, totalHeight)

        // TODO(calintat): Do something when these do not satisfy constraints.
        val tableSize = constraints.constrain(IntSize(tableWidth, tableHeight))

        val rowBackgroundPlaceables = rowBackgroundMeasurables.mapIndexed { index, measurable ->
            measurable.measure(
                Constraints(
                    minWidth = tableSize.width,
                    maxWidth = tableSize.width,
                    minHeight = rowHeights[index],
                    maxHeight = rowHeights[index]
                )
            )
        }

        layout(tableSize.width, tableSize.height) {
            for (row in 1 until rowCount) {
                val rowOffset = rowOffsets[row]
                val y = rowOffset - state.offset
                val height = rowHeights[row]
                if (y > -height && y < state.viewportHeight) {
                    // Place backgrounds
                    rowBackgroundPlaceables[row].place(x = 0, y = y)
                    // Place cells
                    for (column in 0 until columnCount) {
                        placeables[row][column]?.let {
                            val position = columns[column].alignment.align(
                                it.width, columnWidths[column], layoutDirection
                            )
                            it.place(
                                x = columnOffsets[column] + position,
                                y = y
                            )
                        }
                    }

                    // Place separators
                    separatorPlaceables[row].let {
                        it.place(x = 0, y = y + rowHeights[row] - it.height)
                    }
                }
            }

            // Draw header
            rowBackgroundPlaceables[0].place(x = 0, y = 0)
            for (column in 0 until columnCount) {
                placeables[0][column]?.let {
                    val position = columns[column].alignment.align(
                        it.width, columnWidths[column], layoutDirection
                    )
                    it.place(
                        x = columnOffsets[column] + position,
                        y = 0
                    )
                }
            }

            // Place header separator
            separatorPlaceables[0].let {
                it.place(x = 0, y = rowHeights[0] - it.height)
            }

            // Place footer
            if (footerPlaceable != null) {
                rowBackgroundPlaceables[rowCount].place(x = 0, y = state.viewportHeight - footerPlaceable.height)
            }
            footerPlaceable?.place(x = 0, y = state.viewportHeight - footerPlaceable.height)
        }
    }
}
