package com.seanproctor.datatable.material3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.paging.BasicPaginatedDataTable
import com.seanproctor.datatable.paging.PageSize
import com.seanproctor.datatable.paging.PaginatedDataTableState
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import com.seanproctor.datatable_material3.generated.resources.Res
import com.seanproctor.datatable_material3.generated.resources.chevron_left
import com.seanproctor.datatable_material3.generated.resources.chevron_right
import com.seanproctor.datatable_material3.generated.resources.first_page
import com.seanproctor.datatable_material3.generated.resources.last_page
import org.jetbrains.compose.resources.painterResource
import kotlin.math.min

/**
 * A composable function that renders a Material 3 styled paginated data table with support for
 * dynamic page sizing based on available viewport height.
 *
 * This table automatically calculates how many rows can fit in the available vertical space when
 * using [PageSize.FitHeight], making it ideal for displaying large datasets without scrolling.
 * The table handles pagination state management and dynamically adjusts the current page when
 * the page size changes.
 *
 * @param columns List of column definitions specifying width, alignment, header content,
 * and optional sort handlers for each column.
 * @param state Pagination state managing current page, page size, and total item count.
 * Use [rememberPaginatedDataTableState] to create.
 * @param modifier Modifier to be applied to the table container.
 * @param separator Composable function to render separators between rows. Defaults to no separator.
 * @param headerHeight Height of the header row. Defaults to [Dp.Unspecified] for automatic sizing.
 * Must be specified when using [PageSize.FitHeight].
 * @param rowHeight Height of data rows. Defaults to [Dp.Unspecified] for automatic sizing.
 * Must be specified when using [PageSize.FitHeight].
 * @param contentPadding Padding applied to the content within each cell.
 * Defaults to 16.dp horizontal padding.
 * @param headerBackgroundColor Background color for the header row. Defaults to [Color.Unspecified].
 * @param footerBackgroundColor Background color for the footer section. Defaults to [Color.Unspecified].
 * @param sortColumnIndex Zero-based index of the currently sorted column, or null if no column is sorted.
 * @param sortAscending Whether the sorted column is in ascending order. Defaults to true.
 * @param logger Optional logging function for debugging table layout measurements.
 * @param content Lambda with [DataTableScope] receiver that defines table rows for the current page.
 * Receives [fromIndex] (inclusive) and [toIndex] (exclusive) parameters indicating which data items
 * should be rendered on the current page.
 *
 * @sample
 * ```
 * BasicPaginatedDataTable(
 *     columns = listOf(
 *         DataColumn(width = ColumnWidth.Fixed(100.dp)) { Text("ID") },
 *         DataColumn(width = ColumnWidth.Flex(1f)) { Text("Name") }
 *     ),
 *     state = rememberPaginatedDataTableState(
 *         count = 100,
 *         pageSize = PageSize.FitHeight
 *     ),
 *     headerHeight = 56.dp,
 *     rowHeight = 52.dp,
 *     footer = {
 *         PaginationControls(state = paginationState)
 *     }
 * ) { fromIndex, toIndex ->
 *     for (i in fromIndex until toIndex) {
 *         row {
 *             cell { Text("$i") }
 *             cell { Text("Item $i") }
 *         }
 *     }
 * }
 * ```
 *
 * @see BasicPaginatedDataTable for a lower-level paginated table implementation
 * @see PaginatedDataTableState for pagination state management
 * @see PageSize for available page sizing strategies
 */
@Composable
fun PaginatedDataTable(
    columns: List<DataColumn>,
    state: PaginatedDataTableState,
    modifier: Modifier = Modifier,
    separator: @Composable () -> Unit = { HorizontalDivider() },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    footerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.(fromIndex: Int, toIndex: Int) -> Unit,
) {
    BasicPaginatedDataTable(
        columns = columns,
        modifier = modifier,
        separator = separator,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        contentPadding = contentPadding,
        headerBackgroundColor = headerBackgroundColor,
        footerBackgroundColor = footerBackgroundColor,
        state = state,
        footer = {
            Row(
                modifier = Modifier.height(rowHeight).padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val start = min(state.currentPageIndex * state.currentPageSize + 1, state.count)
                val end = min(start + state.currentPageSize - 1, state.count)
                val pageCount = (state.count + state.currentPageSize - 1) / state.currentPageSize
                Text("$start-$end of ${state.count}")
                IconButton(
                    onClick = { state.currentPageIndex = 0 },
                    enabled = state.currentPageIndex > 0,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.first_page),
                        contentDescription = "First page",
                    )
                }
                IconButton(
                    onClick = { state.currentPageIndex-- },
                    enabled = state.currentPageIndex > 0,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.chevron_left),
                        contentDescription = "Previous page",
                    )
                }
                IconButton(
                    onClick = { state.currentPageIndex++ },
                    enabled = state.currentPageIndex < pageCount - 1
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.chevron_right),
                        contentDescription = "Next page",
                    )
                }
                IconButton(
                    onClick = { state.currentPageIndex = pageCount - 1 },
                    enabled = state.currentPageIndex < pageCount - 1
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.last_page),
                        contentDescription = "Last page",
                    )
                }
            }
        },
        cellContentProvider = Material3CellContentProvider,
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        logger = logger,
        content = content
    )
}
