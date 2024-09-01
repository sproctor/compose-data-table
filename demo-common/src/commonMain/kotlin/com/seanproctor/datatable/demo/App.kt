package com.seanproctor.datatable.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.material3.DataTable
import com.seanproctor.datatable.material3.PaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(onRowClick: (Int) -> Unit) {
    Column {
        var selectedIndex by remember { mutableStateOf(0) }
        PrimaryTabRow(
            selectedTabIndex = selectedIndex
        ) {
            val titles = listOf("Normal", "Paginated")
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

        if (selectedIndex == 0) {
            DataTable(
                columns = columns,
                sortColumnIndex = sortColumnIndex,
                sortAscending = sortAscending,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                logger = { println(it) }
            ) {
                generateTable(sortedData, onRowClick)
            }
        } else {
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