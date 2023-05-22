import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
        Column {
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
                modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
            ) {
                row {
                    onClick = {
                        println("First row clicked")
                    }
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
                    onClick = {
                        println("Second row clicked")
                    }
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
                row {
                    cell {
                        Text("Seven")
                    }
                    cell {
                        Text("Eight")
                    }
                    cell {
                        Text("9.0")
                    }
                }
            }
            Text("after table")
        }
    }
}
