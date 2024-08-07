package com.seanproctor.datatable.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.paging.BasicPaginatedDataTable
import com.seanproctor.datatable.paging.PaginatedDataTableState
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import kotlin.math.min

@Composable
fun PaginatedDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { Divider() },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    state: PaginatedDataTableState = rememberPaginatedDataTableState(10),
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    content: DataTableScope.() -> Unit,
) {
    BasicPaginatedDataTable(
        columns = columns,
        modifier = modifier,
        separator = separator,
        headerHeight = headerHeight,
        horizontalPadding = horizontalPadding,
        state = state,
        footer = {
            Row(
                modifier = Modifier.height(rowHeight).padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val start = min(state.pageIndex * state.pageSize + 1, state.count)
                val end = min(start + state.pageSize - 1, state.count)
                val pageCount = (state.count + state.pageSize - 1) / state.pageSize
                Text("$start-$end of ${state.count}")
                IconButton(
                    onClick = { state.pageIndex = 0 },
                    enabled = state.pageIndex > 0,
                ) {
                    Icon(Icons.Default.FirstPage, "First")
                }
                IconButton(
                    onClick = { state.pageIndex-- },
                    enabled = state.pageIndex > 0,
                ) {
                    Icon(Icons.Default.ChevronLeft, "Previous")
                }
                IconButton(
                    onClick = { state.pageIndex++ },
                    enabled = state.pageIndex < pageCount - 1
                ) {
                    Icon(Icons.Default.ChevronRight, "Next")
                }
                IconButton(
                    onClick = { state.pageIndex = pageCount - 1 },
                    enabled = state.pageIndex < pageCount - 1
                ) {
                    Icon(Icons.AutoMirrored.Filled.LastPage, "Last")
                }
            }
        },
        cellContentProvider = MaterialCellContentProvider,
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        content = content
    )
}