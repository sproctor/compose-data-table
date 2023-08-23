package com.seanproctor.datatable.paging

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.*

@Composable
fun BasicPaginatedDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    state: PaginatedDataTableState = rememberPaginatedDataTableState(10),
    footer: @Composable () -> Unit = { },
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    showCheckboxColumn: Boolean = false,
    selectedRows: Set<Int> = emptySet(),
    onSelectAll: (Boolean) -> Unit = {},
    onRowSelected: (Int, Boolean) -> Unit = { _, _ -> },
    content: DataTableScope.() -> Unit
) {
    BasicDataTable(
        columns = columns,
        modifier = modifier,
        separator = separator,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        horizontalPadding = horizontalPadding,
        footer = footer,
        cellContentProvider = cellContentProvider,
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        showCheckboxColumn = showCheckboxColumn,
        selectedRows = selectedRows,
        onSelectAll = onSelectAll,
        onRowSelected = onRowSelected,
    ) {
        val start = state.pageIndex * state.pageSize
        val scope = PaginatedRowScope(start, start + state.pageSize, this)
        with(scope) {
            content()
        }
        if (state.count != scope.index) {
            state.count = scope.index
        }
    }
}
