package com.seanproctor.datatable.demo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.material.PaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState

@Composable
fun App(onRowClick: (Int) -> Unit) {
    PaginatedDataTable(
        columns = listOf(
            DataColumn {
                Text("Column1")
            },
            DataColumn {
                Text("Column2")
            },
            DataColumn(Alignment.CenterEnd) {
                Text("Column3")
            },
        ),
        state = rememberPaginatedDataTableState(5),
        modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth(),
    ) {
        for (rowIndex in 0 until 100) {
            row {
                onClick = { onRowClick(rowIndex) }
                cell {
                    Text("Row ${rowIndex + 1}, Column 1", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                cell {
                    Text("Row ${rowIndex + 1}, Column 2", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                cell {
                    Text((rowIndex + 1f).toString())
                }
            }
        }
    }
}
