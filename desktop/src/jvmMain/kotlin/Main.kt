import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.seanproctor.datatable.Table


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        Row {
            Table(
//            modifier = Modifier.fillMaxSize(),
                alignment = {
                    if (it == 2) Alignment.BottomEnd else Alignment.CenterStart
                },
                columns = 3,
            ) {
                headerRow {
                    cell {
                        Text("Column1")
                    }
                    cell {
                        Text("Column2")
                    }
                    cell {
                        Text("Column3")
                    }
                }
                row {
                    cell {
                        Text("One")
                    }
                    cell {
                        Text("Two")
                    }
                    cell {
                        Text("Three")
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
                        Text("Six")
                    }
                }
            }
            Text("after table")
        }
    }
}
