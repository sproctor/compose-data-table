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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.() -> Unit
) {
    val headerIndexes = mutableListOf<Int>()
    val footerIndexes = mutableListOf<Int>()

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
            if (rowData.isHeader) {
                headerIndexes.add(rowIndex)
            }
            if (rowData.isFooter) {
                footerIndexes.add(rowIndex)
            }
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
            Box {
                separator(row)
            }
        }
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
            .clip(RectangleShape)
            .then(state.awaitLayoutModifier)
            .scrollable(
                state = state,
                orientation = Orientation.Vertical,
                interactionSource = state.internalInteractionSource,
            )
    ) { (contentMeasurables, separatorMeasurables, rowBackgroundMeasurables, footerMeasurables), constraints ->
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

        val tableWidth = listOf(constraints.minWidth, columnWidths.sum()).max()

        val columnAlignment = Array(columnCount) { columns[it].alignment }

        // Measure the remaining children and calculate row heights.
        val measuredRows = mutableListOf<DataTableMeasuredElement>()
        for (row in 0 until rowCount) {
            val isHeader = row == 0 || headerIndexes.contains(row - 1)
            val isFooter = footerIndexes.contains(row - 1)
            for (column in 0 until columnCount) {
                val placeable = placeables[row][column]
                if (placeable == null) {
                    placeables[row][column] = measurableAt(row, column)?.measure(
                        Constraints(minWidth = 0, maxWidth = columnWidths[column])
                    )
                }
            }
            val measuredRow = DataTableMeasuredRow(
                placeables = placeables[row],
                columnWidths = columnWidths,
                columnAlignment = columnAlignment,
                layoutDirection = layoutDirection,
                isHeader = isHeader,
                isFooter = isFooter,
                logger = logger,
            )
            measuredRow.background = rowBackgroundMeasurables[row].measure(
                Constraints(
                    minWidth = tableWidth,
                    maxWidth = tableWidth,
                    minHeight = measuredRow.height,
                    maxHeight = measuredRow.height
                )
            )
            measuredRows.add(measuredRow)
            measuredRows.add(
                DataTableMeasuredSimple(
                    placeables = arrayOf(
                        separatorMeasurables[row].measure(Constraints(minWidth = 0, maxWidth = tableWidth))
                    ),
                    isHeader = isHeader,
                    isFooter = isFooter,
                    logger = logger,
                )
            )
        }

        val footerPlaceables = footerMeasurables
            .map {
                it.measure(
                    Constraints(
                        minWidth = max(
                            constraints.minWidth,
                            tableWidth
                        )
                    )
                )
            }
            .toTypedArray()
        val footerRow = DataTableMeasuredSimple(
            placeables = footerPlaceables,
            isHeader = false,
            isFooter = true,
            logger = logger,
        )
            .also {
                it.background = rowBackgroundMeasurables.last().measure(
                    Constraints(
                        minWidth = tableWidth,
                        maxWidth = tableWidth,
                        minHeight = it.height,
                        maxHeight = it.height
                    )
                )
            }
        measuredRows.add(footerRow)

        val totalHeight = measuredRows.sumOf { it.height }
        state.totalHeight = totalHeight
        val tableHeight = max(constraints.minHeight, totalHeight)

        // TODO(sproctor): Do something when we don't fit in the width
        val tableSize = constraints.constrain(IntSize(tableWidth, tableHeight))

        logger?.invoke("Data table size: $tableSize")

        layout(tableSize.width, tableSize.height) {
            var offset = 0
            var headerOffset = 0
            var footerOffset = state.viewportHeight
            measuredRows.forEach { row ->
                logger?.invoke("Row height: ${row.height}")
                if (row.isHeader) {
                    row.position(headerOffset)
                    headerOffset += row.height
                } else if (row.isFooter) {
                    footerOffset -= row.height
                    row.position(footerOffset)
                } else {
                    val y = offset - state.offset
                    offset += row.height
                    if (y > -row.height && y < state.viewportHeight) {
                        row.position(y + headerOffset)
                        row.place(this)
                    }
                }
            }
            // Place headers and footers last
            measuredRows.forEach { row ->
                if (row.isHeader || row.isFooter) {
                    row.place(this)
                }
            }
        }
    }
}
