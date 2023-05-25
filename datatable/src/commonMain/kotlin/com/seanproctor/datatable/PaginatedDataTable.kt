package com.seanproctor.datatable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.LastPage
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PaginatedDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { Divider() },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    state: PaginatedDataTableState = rememberPaginatedDataTableState(10),
    content: DataTableScope.() -> Unit
) {
    val start = state.pageIndex * state.pageSize
    val end = start + state.pageSize - 1
    var count by remember { mutableStateOf(0) }

    DataTable(
        columns = columns,
        modifier = modifier,
        separator = separator,
        headerHeight = headerHeight,
        horizontalPadding = horizontalPadding,
        footer = {
            Row(
                modifier = Modifier.height(rowHeight).padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val pageCount = (count + state.pageSize - 1) / state.pageSize
                Text("${start + 1}-${end + 1} of $count")
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
                    Icon(Icons.Default.LastPage, "Last")
                }
            }
        }
    ) {
        val scope = PaginatedRowScope(start, start + state.pageSize, this)
        with(scope) {
            content()
        }
        if (count != scope.index) {
            count = scope.index
            println("setting count: $count")
        }
    }
}

private class PaginatedRowScope(
    private val from: Int,
    private val to: Int,
    private val parentScope: DataTableScope,
) : DataTableScope {
    var index: Int = 0

    override fun row(content: TableRowScope.() -> Unit) {
        if (index in from .. to) {
            parentScope.row(content)
        }
        index++
    }

    override fun rows(count: Int, content: TableRowScope.(Int) -> Unit) {
        TODO("Not yet implemented")
    }
}

class PaginatedDataTableState(
    pageSize: Int,
    pageIndex: Int,
) {
    var pageSize by mutableStateOf(pageSize)
    var pageIndex by mutableStateOf(pageIndex)

    companion object {
        val Saver: Saver<PaginatedDataTableState, *> = listSaver(
            save = { listOf(it.pageIndex, it.pageSize) },
            restore = {
                PaginatedDataTableState(it[0], it[1])
            }
        )
    }
}

@Composable
fun rememberPaginatedDataTableState(
    initialPageSize: Int,
    initialPageIndex: Int = 0,
): PaginatedDataTableState {
    return rememberSaveable(saver = PaginatedDataTableState.Saver) {
        PaginatedDataTableState(initialPageSize, initialPageIndex)
    }
}