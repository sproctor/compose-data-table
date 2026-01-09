package com.seanproctor.datatable.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
    footer: @Composable (Int) -> Unit = { },
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.() -> Unit
) {
    var pageSize by remember { mutableStateOf(state.pageSize) }
    val density = LocalDensity.current

    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                /**
                 * The table size is calculated to fit the screen height.
                 * This is if pageSize is equal to 'PAGE_SIZE_FIXED_FLAG (-1)'
                 *
                 * Otherwise, the table has a fixed size
                 */
                if (state.pageSize == PAGE_SIZE_FIXED_FLAG) {
                    val heightPx = coords.size.height.toFloat()
                    val rowHeightPx = with(density) { rowHeight.toPx() }
                    val rows = (heightPx / rowHeightPx).toInt()
                    pageSize = rows - 3
                }
            }
    ) {
        BasicDataTable(
            columns = columns,
            modifier = modifier,
            state = remember(pageSize, state.pageIndex) { DataTableState() },
            separator = separator,
            headerHeight = headerHeight,
            rowHeight = rowHeight,
            contentPadding = contentPadding,
            headerBackgroundColor = headerBackgroundColor,
            footerBackgroundColor = footerBackgroundColor,
            footer = { footer(pageSize) },
            cellContentProvider = cellContentProvider,
            sortColumnIndex = sortColumnIndex,
            sortAscending = sortAscending,
            logger = logger,
        ) {
            val start = state.pageIndex * pageSize
            val scope = PaginatedRowScope(start, start + pageSize, this)
            content(scope)
            if (state.count != scope.index) {
                state.count = scope.index
            }
        }
    }
}
