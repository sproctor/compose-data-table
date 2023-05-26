package com.seanproctor.datatable.paging

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.BasicDataTable
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope

@Composable
fun BasicPaginatedDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    state: PaginatedDataTableState = rememberPaginatedDataTableState(10),
    footer: (@Composable () -> Unit)? = null,
    content: DataTableScope.() -> Unit
) {
    val start = state.pageIndex * state.pageSize
    var count by remember { mutableStateOf(0) }

    BasicDataTable(
        columns = columns,
        modifier = modifier,
        separator = separator,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        horizontalPadding = horizontalPadding,
        footer = footer
    ) {
        val scope = PaginatedRowScope(start, start + state.pageSize, this)
        with(scope) {
            content()
        }
        if (count != scope.index) {
            count = scope.index
        }
    }
}
