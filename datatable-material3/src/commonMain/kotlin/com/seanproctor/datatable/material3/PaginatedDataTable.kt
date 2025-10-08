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
import com.seanproctor.datatable.paging.PaginatedDataTableState
import com.seanproctor.datatable_material3.generated.resources.Res
import com.seanproctor.datatable_material3.generated.resources.chevron_left
import com.seanproctor.datatable_material3.generated.resources.chevron_right
import com.seanproctor.datatable_material3.generated.resources.first_page
import com.seanproctor.datatable_material3.generated.resources.last_page
import org.jetbrains.compose.resources.painterResource
import kotlin.math.min

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
    content: DataTableScope.() -> Unit,
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
                val start = min(state.pageIndex * state.pageSize + 1, state.count)
                val end = min(start + state.pageSize - 1, state.count)
                val pageCount = (state.count + state.pageSize - 1) / state.pageSize
                Text("$start-$end of ${state.count}")
                IconButton(
                    onClick = { state.pageIndex = 0 },
                    enabled = state.pageIndex > 0,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.first_page),
                        contentDescription = "First page",
                    )
                }
                IconButton(
                    onClick = { state.pageIndex-- },
                    enabled = state.pageIndex > 0,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.chevron_left),
                        contentDescription = "Previous page",
                    )
                }
                IconButton(
                    onClick = { state.pageIndex++ },
                    enabled = state.pageIndex < pageCount - 1
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.chevron_right),
                        contentDescription = "Next page",
                    )
                }
                IconButton(
                    onClick = { state.pageIndex = pageCount - 1 },
                    enabled = state.pageIndex < pageCount - 1
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
