package com.seanproctor.datatable.demo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.material3.DataTable

@Composable
fun App(onRowClick: (Int) -> Unit) {
    val rowData = (0 until 30).map { index ->
        DemoData(index + 1f, "Row: $index")
    }
    var sortColumnIndex by remember { mutableStateOf<Int?>(null) }
    var sortAscending by remember { mutableStateOf(true) }

    val sortedData = when (sortColumnIndex) {
        null -> rowData
        0 -> if (sortAscending) rowData.sortedBy { it.text } else rowData.sortedByDescending { it.text }
        1 -> if (sortAscending) rowData.sortedBy { it.value } else rowData.sortedByDescending { it.value }
        else -> throw IllegalStateException("Invalid column index")
    }

    LazyColumn {  }
    DataTable(
        columns = listOf(
            DataColumn(
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
            },
        ),
        //state = rememberPaginatedDataTableState(15),
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        logger = { println(it) }
    ) {
        sortedData.forEachIndexed { index, data ->
            row {
                onClick = { onRowClick(index) }
                cell {
                    Text(data.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                cell {
                    Text(data.value.toString())
                }
            }
        }
        row {
            isFooter = true
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
}

data class DemoData(val value: Float, val text: String)