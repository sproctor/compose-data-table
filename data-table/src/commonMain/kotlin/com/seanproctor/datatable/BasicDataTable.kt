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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.*
import kotlin.math.max
import kotlin.math.roundToInt

private data class RowKey(val row: Int)

private enum class SlotsEnum {
    Main,
    Footer,
    FooterBackground,
}

/**
 * Layout model that arranges its children into rows and columns.
 */
@Composable
fun BasicDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    state: DataTableState = rememberDataTableState(),
    separator: @Composable () -> Unit = { },
    headerHeight: Dp = Dp.Unspecified,
    rowHeight: Dp = Dp.Unspecified,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headerBackgroundColor: Color = Color.Unspecified,
    footerBackgroundColor: Color = Color.Unspecified,
    rowBackgroundColor: @Composable (Int) -> Color = { Color.Unspecified },
    footer: (@Composable () -> Unit)? = null,
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.() -> Unit
) {
    val headerIndexes = mutableListOf<Int>()
    val footerIndexes = mutableListOf<Int>()

    val tableScope = DataTableScopeImpl(content)
    val cellContents = @Composable {
        with(tableScope) {
            columns.forEachIndexed { columnIndex, columnDefinition ->
                val sorted = columnIndex == sortColumnIndex
                cellContentProvider.HeaderCellContent(
                    sorted = sorted,
                    sortAscending = sortAscending,
                    isSortIconTrailing = columnDefinition.isSortIconTrailing,
                    onClick = columnDefinition.onSort?.let {
                        { it(columnIndex, if (sorted) !sortAscending else sortAscending) }
                    }
                ) {
                    // Must have exactly 1 Composable per cell
                    Box {
                        columnDefinition.header()
                    }
                }
            }

            tableRows.forEachIndexed { rowIndex, rowScope ->
                with(rowScope) {
                    if (isHeader) {
                        headerIndexes.add(rowIndex)
                    }
                    if (isFooter) {
                        footerIndexes.add(rowIndex)
                    }
                    check(cells.size <= columns.size) { "Row $rowIndex has too many cells." }
                    check(cells.size >= columns.size) { "Row $rowIndex doesn't have enough cells." }
                    cells.forEach { cellData ->
                        // Must have exactly 1 Composable per cell
                        Box(Modifier.padding(contentPadding)) {
                            cellContentProvider.RowCellContent {
                                cellData()
                            }
                        }
                    }
                }
            }
        }
    }

    val layoutDirection = LocalLayoutDirection.current
    val columnCount = columns.size
    val rowCount = tableScope.tableRows.size + 1

    SubcomposeLayout(
        modifier
            .clip(RectangleShape)
            .then(state.awaitLayoutModifier)
            .scrollable(
                state = state,
                orientation = Orientation.Vertical,
                interactionSource = state.internalInteractionSource,
            )
    ) { constraints ->
        val cellMeasurables = subcompose(SlotsEnum.Main, cellContents)
        fun measurableAt(row: Int, column: Int) = cellMeasurables[row * columnCount + column]
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
                            ?: measurableAt(row, column).measure(Constraints())
                                .also { placeables[row][column] = it }.width
                    },
                    minIntrinsicWidth = {
                        val heightDp = if (row == 0) {
                            headerHeight
                        } else {
                            rowHeight
                        }
                        measurableAt(row, column).minIntrinsicWidth(heightDp.roundToPxOrInf(this))
                    },
                    maxIntrinsicWidth = {
                        val heightDp = if (row == 0) {
                            headerHeight
                        } else {
                            rowHeight
                        }
                        measurableAt(row, column).maxIntrinsicWidth(heightDp.roundToPxOrInf(this))
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
                    placeables[row][column] = measurableAt(row, column).measure(
                        Constraints(minWidth = 0, maxWidth = columnWidths[column])
                    )
                }
            }
            val measuredRow = DataTableMeasuredRow(
                placeables = placeables[row],
                rowHeight = if (rowHeight.isSpecified) rowHeight.roundToPx() else null,
                key = RowKey(row),
                columnWidths = columnWidths,
                columnAlignment = columnAlignment,
                tableWidth = tableWidth,
                layoutDirection = layoutDirection,
                isHeader = isHeader,
                isFooter = isFooter,
                logger = logger,
                background = {
                    if (row == 0) {
                        Box(
                            Modifier
                                .background(headerBackgroundColor)
                                .fillMaxSize()
                        ) {
                            Box(Modifier.align(Alignment.BottomStart)) {
                                separator()
                            }
                        }
                    } else {
                        val rowData = tableScope.tableRows[row - 1]
                        Box(Modifier
                            .background(rowBackgroundColor(row - 1))
                            .fillMaxSize()
                            .then(if (rowData.onClick != null) Modifier.clickable { rowData.onClick?.invoke() } else Modifier)
                        ) {
                            if (row < rowCount - 1) {
                                Box(Modifier.align(Alignment.BottomStart)) {
                                    separator()
                                }
                            }
                        }
                    }
                }
            )
            measuredRows.add(measuredRow)
        }

        if (footer != null) {
            val footerPlaceables = subcompose(SlotsEnum.Footer, footer)
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
                key = SlotsEnum.FooterBackground,
                isHeader = false,
                isFooter = true,
                tableWidth = tableWidth,
                background = {
                    Box(
                        Modifier
                            .background(footerBackgroundColor)
                            .fillMaxSize()
                    )
                },
                logger = logger,
            )
            measuredRows.add(footerRow)
        }

        val totalHeight = measuredRows.sumOf { it.height }
        state.totalHeight = totalHeight
        val tableHeight = max(constraints.minHeight, totalHeight)

        // TODO(sproctor): Do something when we don't fit in the width
        val tableSize = constraints.constrain(IntSize(tableWidth, tableHeight))

        logger?.invoke("Data table size: $tableSize")

        state.viewportHeight = tableSize.height

        layout(tableSize.width, tableSize.height) {
            var offset = 0
            var headerOffset = 0
            var footerOffset = state.viewportHeight
            measuredRows.forEach { row ->
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
                        row.place(this@SubcomposeLayout, this)
                    }
                }
            }
            // Place headers and footers last
            measuredRows.forEach { row ->
                if (row.isHeader || row.isFooter) {
                    row.place(this@SubcomposeLayout, this)
                }
            }
        }
    }
}

fun Dp.roundToPxOrInf(density: Density): Int {
    return if (isSpecified && isFinite) {
        with(density) {
            roundToPx()
        }
    } else {
        Int.MAX_VALUE
    }
}
