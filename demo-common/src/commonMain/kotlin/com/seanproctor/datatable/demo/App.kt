package com.seanproctor.datatable.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.*
import com.seanproctor.datatable.material3.DataTable
import com.seanproctor.datatable.material3.LazyPaginatedDataTable
import com.seanproctor.datatable.material3.PaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(onRowClick: (Int) -> Unit) {
    Column {
        var selectedIndex by remember { mutableStateOf(0) }
        PrimaryTabRow(
            selectedTabIndex = selectedIndex
        ) {
            val titles = listOf("Normal", "Paginated", "Lazy paginated")
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index },
                    text = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
        val rowData = (0 until 30).map { index ->
            DemoData(index + 1f, "index: $index")
        }
        var sortColumnIndex by remember { mutableStateOf<Int?>(null) }
        var sortAscending by remember { mutableStateOf(true) }

        val sortedData = when (sortColumnIndex) {
            null -> rowData
            0 -> if (sortAscending) rowData.sortedBy { it.text } else rowData.sortedByDescending { it.text }
            1 -> if (sortAscending) rowData.sortedBy { it.value } else rowData.sortedByDescending { it.value }
            else -> throw IllegalStateException("Invalid column index")
        }

        val columns = listOf(
            DataColumn(width = TableColumnWidth.Wrap) { },
            DataColumn(
                width = TableColumnWidth.Wrap,
                onSort = { index, ascending ->
                    sortColumnIndex = index
                    sortAscending = ascending
                }
            ) {
                Text("Column1")
            },
            DataColumn(
                alignment = Alignment.CenterEnd,
                isSortIconTrailing = false,
                onSort = { index, ascending ->
                    sortColumnIndex = index
                    sortAscending = ascending
                }
            ) {
                Text("Column2")
            }
        )

        val scrollState = remember(selectedIndex) { DataTableState() }
        if (selectedIndex == 0) {
            Box {
                DataTable(
                    columns = columns,
                    state = scrollState,
                    sortColumnIndex = sortColumnIndex,
                    sortAscending = sortAscending,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
//                    logger = { println(it) }
                ) {
                    generateTable(sortedData, onRowClick)
                }
                LaunchedEffect(scrollState.horizontalScrollState.viewportSize) {
                    println("viewport: ${scrollState.horizontalScrollState.viewportSize}")
                    println("total size: ${scrollState.horizontalScrollState.totalSize}")
                }
                VerticalScrollbar(scrollState.verticalScrollState, Modifier.fillMaxHeight().align(Alignment.CenterEnd))
                HorizontalScrollbar(scrollState.horizontalScrollState, Modifier.fillMaxWidth().align(Alignment.BottomCenter))
            }
        } else if (selectedIndex == 1) {
            PaginatedDataTable(
                columns = columns,
                state = rememberPaginatedDataTableState(5),
                sortColumnIndex = sortColumnIndex,
                sortAscending = sortAscending,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                logger = { println(it) }
            ) {
                generateTable(sortedData, onRowClick)
            }
        } else {
            LazyPaginatedDataTable(
                columns = columns,
                state = rememberPaginatedDataTableState(5),
                sortColumnIndex = sortColumnIndex,
                sortAscending = sortAscending,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                logger = { println(it) },
                fetchPage = { state ->
                    val fromIndex = state.pageIndex * state.pageSize
                    val toIndex = min(fromIndex + state.pageSize, state.count-1)
                    val subset = sortedData.subList(fromIndex, toIndex)
                    subset.forEachIndexed { index, rowData ->
                        row {
                            onClick = { onRowClick(fromIndex + index) }
                            cell { }
                            cell {
                                Text(rowData.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            cell {
                                Text(rowData.value.toString())
                            }
                        }
                    }
                }
            )
        }
    }
}

fun DataTableScope.generateTable(data: List<DemoData>, onRowClick: (Int) -> Unit) {
    data.forEachIndexed { index, rowData ->
        row {
            onClick = { onRowClick(index) }
            cell { }
            cell {
                Text(rowData.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            cell {
                Text(rowData.value.toString())
            }
        }
    }
    // Footer does not work well with PaginatedDataTable
    row {
        isFooter = true
        cell { }
        cell { }
        cell {
            Text(
                text = "Footer",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

data class DemoData(val value: Float, val text: String)