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

package com.seanproctor.datatable.material3

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.BasicDataTable
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.rememberDataTableState

/**
 * A composable function that renders a Material 3 styled customizable data table with support
 * for scrolling, sorting, headers, footers, and flexible column sizing.
 *
 * This is a convenience wrapper around [BasicDataTable] that applies Material 3 styling
 * through [Material3CellContentProvider]. It provides sensible defaults for common use cases
 * including horizontal dividers between rows and standard Material 3 sizing.
 *
 * @param columns List of column definitions specifying width, alignment, header content,
 * and optional sort behavior for each column.
 * @param modifier Modifier to be applied to the table container.
 * @param state State object for managing scroll positions and table state.
 * Use [rememberDataTableState] to create and persist across recompositions.
 * @param separator Composable function to render between rows. Defaults to [HorizontalDivider].
 * @param headerHeight Height of the header row. Defaults to 56.dp per Material 3 guidelines.
 * @param rowHeight Height of data rows. Defaults to 52.dp per Material 3 guidelines.
 * @param contentPadding Padding applied inside each table cell. Defaults to 16.dp horizontal padding.
 * @param headerBackgroundColor Background color for the header row. Defaults to [Color.Unspecified].
 * @param footerBackgroundColor Background color for the footer section. Defaults to [Color.Unspecified].
 * @param footer Composable function to render footer content below the table. Defaults to empty content.
 * @param sortColumnIndex Zero-based index of the currently sorted column, or null if unsorted.
 * @param sortAscending Whether the sort order is ascending. Only applies when [sortColumnIndex] is set.
 * Defaults to true.
 * @param logger Optional callback for logging table layout measurements during development.
 * @param content Lambda with [DataTableScope] receiver that defines the table's row and cell structure.
 *
 * @sample
 * ```
 * DataTable(
 *     columns = listOf(
 *         DataColumn(width = ColumnWidth.Fixed(100.dp)) { Text("Name") },
 *         DataColumn(width = ColumnWidth.Flex(1f)) { Text("Description") }
 *     )
 * ) {
 *     row {
 *         cell { Text("John Doe") }
 *         cell { Text("Software Engineer") }
 *     }
 * }
 * ```
 *
 * @see BasicDataTable for a lower-level table implementation with custom cell providers
 * @see DataTableScope for available row configuration options
 */
@Composable
fun DataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    state: DataTableState = rememberDataTableState(),
    separator: @Composable () -> Unit = { HorizontalDivider() },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headerBackgroundColor: Color = Color.Unspecified,
    footerBackgroundColor: Color = Color.Unspecified,
    footer: @Composable () -> Unit = { },
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.() -> Unit
) {
    BasicDataTable(
        columns = columns,
        modifier = modifier,
        state = state,
        separator = separator,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        contentPadding = contentPadding,
        headerBackgroundColor = headerBackgroundColor,
        footerBackgroundColor = footerBackgroundColor,
        footer = footer,
        cellContentProvider = Material3CellContentProvider,
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        logger = logger,
        content = content
    )
}
