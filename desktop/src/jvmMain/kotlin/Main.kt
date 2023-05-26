import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.material.PaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
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
                    onClick = { println("Row clicked: $rowIndex") }
                    cell {
                        Text((rowIndex * 3).toString())
                    }
                    cell {
                        Text((rowIndex * 3 + 1).toString(16))
                    }
                    cell {
                        Text((rowIndex * 3.0f + 2.0f).toString())
                    }
                }
            }
        }
    }
}
