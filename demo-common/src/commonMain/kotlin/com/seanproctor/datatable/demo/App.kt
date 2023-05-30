package com.seanproctor.datatable.demo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.material.PaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState

@Composable
fun App(onRowClick: (Int) -> Unit) {
    val rowData = (0 until 100).map { index ->
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

    PaginatedDataTable(
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
                alignment = Alignment.End,
                onSort = { index, ascending ->
                    sortColumnIndex = index
                    sortAscending = ascending
                }
            ) {
                Text("Column2")
            },
        ),
        state = rememberPaginatedDataTableState(5),
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth(),
    ) {
        sortedData.forEach { data ->
            row {
                onClick = { onRowClick(rowIndex) }
                cell {
                    Text(data.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                cell {
                    Text(data.value.toString())
                }
            }
        }
    }
}

data class DemoData(val value: Float, val text: String)