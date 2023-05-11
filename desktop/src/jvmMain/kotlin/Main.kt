import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.seanproctor.datatable.Table
import com.seanproctor.datatable.TableColumnDefinition


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        Row {
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
                )
//            modifier = Modifier.fillMaxSize(),
            ) {
                row {
                    cell {
                        Text("One")
                    }
                    cell {
                        Text("Two")
                    }
                    cell {
                        Text("3.0")
                    }
                }
                row {
                    cell {
                        Text("Four")
                    }
                    cell {
                        Text("Five")
                    }
                    cell {
                        Text("6.0")
                    }
                }
            }
            Text("after table")
        }
    }
}
