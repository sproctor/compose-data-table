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

import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.unit.Constraints
import kotlin.math.max
import kotlin.math.roundToInt

//private val MinIntrinsicWidthMeasureBlock:
//            (Int, (Int) -> TableColumnWidth) -> IntrinsicMeasurable =
//    { columns, columnWidth ->
//        { measurables, availableHeight ->
//            intrinsicWidth(
//                columns = columns,
//                columnWidth = columnWidth,
//                children = measurables,
//                availableHeight = availableHeight,
//                minimise = true
//            )
//        }
//    }
//
//private val MinIntrinsicHeightMeasureBlock:
//            (Int, (Int) -> TableColumnWidth) -> IntrinsicMeasureBlock =
//    { columns, columnWidth ->
//        { measurables, availableWidth, _ ->
//            intrinsicHeight(
//                columns = columns,
//                columnWidth = columnWidth,
//                children = measurables,
//                availableWidth = availableWidth,
//                intrinsicHeight = { w -> minIntrinsicHeight(w) }
//            )
//        }
//    }
//private val MaxIntrinsicWidthMeasureBlock:
//            (Int, (Int) -> TableColumnWidth) -> IntrinsicMeasureBlock =
//    { columns, columnWidth ->
//        { measurables, availableHeight, _ ->
//            intrinsicWidth(
//                columns = columns,
//                columnWidth = columnWidth,
//                children = measurables,
//                availableHeight = availableHeight,
//                minimise = false
//            )
//        }
//    }
//private val MaxIntrinsicHeightMeasureBlock:
//            (Int, (Int) -> TableColumnWidth) -> IntrinsicMeasureBlock =
//    { columns, columnWidth ->
//        { measurables, availableWidth, _ ->
//            intrinsicHeight(
//                columns = columns,
//                columnWidth = columnWidth,
//                children = measurables,
//                availableWidth = availableWidth,
//                intrinsicHeight = { w -> maxIntrinsicHeight(w) }
//            )
//        }
//    }
private fun IntrinsicMeasureScope.intrinsicWidth(
    columns: Int,
    columnWidth: (columnIndex: Int) -> TableColumnWidth,
    children: List<IntrinsicMeasurable>,
    availableHeight: Int,
    minimise: Boolean
): Int {
    val measurables = children.filter { it.rowIndex != null }.groupBy { it.rowIndex }
    val rows = measurables.size
    fun measurableAt(row: Int, column: Int) = measurables[row]?.getOrNull(column)
    var totalFlex = 0f
    var flexibleSpace = 0
    var inflexibleSpace = 0
    for (column in 0 until columns) {
        val spec = columnWidth(column)
        val cells = List(rows) { row ->
            TableMeasurable(
                preferredWidth = { 0 },
                minIntrinsicWidth = {
                    measurableAt(row, column)?.minIntrinsicWidth(it) ?: 0
                },
                maxIntrinsicWidth = {
                    measurableAt(row, column)?.maxIntrinsicWidth(it) ?: 0
                }
            )
        }
        val width = if (minimise) {
            spec.minIntrinsicWidth(cells, Constraints.Infinity, this, availableHeight)
        } else {
            spec.maxIntrinsicWidth(cells, Constraints.Infinity, this, availableHeight)
        }
        if (spec.flexValue <= 0) {
            inflexibleSpace += width
        } else {
            totalFlex += spec.flexValue
            flexibleSpace = max(flexibleSpace, (width / spec.flexValue).roundToInt())
        }
    }
    return (flexibleSpace * totalFlex + inflexibleSpace).roundToInt()
}

private fun IntrinsicMeasureScope.intrinsicHeight(
    columns: Int,
    columnWidth: (columnIndex: Int) -> TableColumnWidth,
    children: List<IntrinsicMeasurable>,
    availableWidth: Int,
    intrinsicHeight: IntrinsicMeasurable.(Int) -> Int
): Int {
    val measurables = children.filter { it.rowIndex != null }.groupBy { it.rowIndex }
    val rows = measurables.size
    fun measurableAt(row: Int, column: Int) = measurables[row]?.getOrNull(column)
    // Compute column widths and collect flex information.
    var totalFlex = 0f
    var availableSpace = availableWidth
    val columnWidths = Array(columns) { 0 }
    for (column in 0 until columns) {
        val spec = columnWidth(column)
        val cells = List(rows) { row ->
            TableMeasurable(
                preferredWidth = { 0 },
                minIntrinsicWidth = {
                    measurableAt(row, column)?.minIntrinsicWidth(it) ?: 0
                },
                maxIntrinsicWidth = {
                    measurableAt(row, column)?.maxIntrinsicWidth(it) ?: 0
                }
            )
        }
        columnWidths[column] =
            spec.maxIntrinsicWidth(cells, availableWidth, this, Constraints.Infinity)
        availableSpace -= columnWidths[column]
        totalFlex += spec.flexValue
    }
    // Grow flexible columns to fill available horizontal space.
    if (totalFlex > 0 && availableSpace > 0) {
        for (column in 0 until columns) {
            val spec = columnWidth(column)
            if (spec.flexValue > 0) {
                columnWidths[column] += (availableSpace * (spec.flexValue / totalFlex)).roundToInt()
            }
        }
    }
    // Calculate row heights and table height.
    return (0 until rows).fold(0) { tableHeight, row ->
        val rowHeight = (0 until columns).fold(0) { rowHeight, column ->
            max(
                rowHeight,
                measurableAt(row, column)?.intrinsicHeight(columnWidths[column])
                    ?: 0
            )
        }
        tableHeight + rowHeight
    }
}