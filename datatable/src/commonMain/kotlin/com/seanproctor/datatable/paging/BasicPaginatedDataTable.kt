package com.seanproctor.datatable.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.BasicDataTable
import com.seanproctor.datatable.CellContentProvider
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.DefaultCellContentProvider
import kotlin.math.min

/**
 * A composable function that renders a paginated data table with support for dynamic page sizing
 * based on available viewport height.
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
 * @param footer Composable function to render footer content below the table (typically pagination controls).
 * Defaults to empty content.
 * @param cellContentProvider Provider for rendering cell content with custom styling and behavior.
 * Defaults to [DefaultCellContentProvider].
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
 * @see BasicDataTable for the underlying non-paginated table implementation
 * @see PaginatedDataTableState for pagination state management
 * @see PageSize for available page sizing strategies
 */
@Composable
fun BasicPaginatedDataTable(
    columns: List<DataColumn>,
    state: PaginatedDataTableState,
    modifier: Modifier = Modifier,
    separator: @Composable () -> Unit = { },
    headerHeight: Dp = Dp.Unspecified,
    rowHeight: Dp = Dp.Unspecified,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headerBackgroundColor: Color = Color.Unspecified,
    footerBackgroundColor: Color = Color.Unspecified,
    footer: @Composable () -> Unit = { },
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.(fromIndex: Int, toIndex: Int) -> Unit
) {
    require(state.pageSize != PageSize.FitHeight || headerHeight != Dp.Unspecified) {
        "headerHeight must be specified when using PageSize.FitHeight"
    }
    require(state.pageSize != PageSize.FitHeight || rowHeight != Dp.Unspecified) {
        "rowHeight must be specified when using PageSize.FitHeight"
    }
    val density = LocalDensity.current
    var footerHeightPx by remember { mutableStateOf(0) }
    var heightPx by remember { mutableStateOf(0) }

    LaunchedEffect(heightPx, footerHeightPx) {
        if (state.pageSize == PageSize.FitHeight) {
            with(density) {
                val rowSpacePx = heightPx.toFloat() - headerHeight.toPx() - footerHeightPx.toFloat()
                val rowHeightPx = rowHeight.toPx()
                val rowCount = (rowSpacePx / rowHeightPx).toInt()
                if (rowCount != state.currentPageSize) {
                    val firstVisible = state.currentPageSize * state.currentPageIndex
                    state.currentPageSize = rowCount
                    state.currentPageIndex = firstVisible / rowCount
                }
            }
        }
    }

    BasicDataTable(
        columns = columns,
        modifier = modifier
            .fillMaxHeight()
            .onGloballyPositioned { coords ->
                heightPx = coords.size.height
                if (state.pageSize == PageSize.FitHeight) {
                    with(density) {
                        val heightPx = coords.size.height.toFloat() - headerHeight.toPx() - footerHeightPx
                        val rowHeightPx = rowHeight.toPx()
                        (heightPx / rowHeightPx).toInt()
                    }
                }
            },
        state = remember(state) { DataTableState() },
        separator = separator,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        contentPadding = contentPadding,
        headerBackgroundColor = headerBackgroundColor,
        footerBackgroundColor = footerBackgroundColor,
        footer = {
            Box(Modifier.onGloballyPositioned { coords ->
                footerHeightPx = coords.size.height
            }) {
                footer()
            }
        },
        cellContentProvider = cellContentProvider,
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        logger = logger,
    ) {
        val fromIndex = state.currentPageSize * state.currentPageIndex
        content(fromIndex, min(fromIndex + state.currentPageSize, state.count))
    }
}
