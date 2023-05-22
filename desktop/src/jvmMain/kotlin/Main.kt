import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.seanproctor.datatable.Table
import com.seanproctor.datatable.TableColumnDefinition


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        Table(
            columns = listOf(
                TableColumnDefinition {
                    Text("Column1")
                },
                TableColumnDefinition {
                    Text("Column2")
                },
                TableColumnDefinition(Alignment.CenterEnd) {
                    Text("Column3")
                },
            ),
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            for (rowIndex in 0 until 10) {
                row {
//                            onClick = { Log.d("datatable", "Row clicked: $rowIndex") }
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
