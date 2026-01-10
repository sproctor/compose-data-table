import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.seanproctor.datatable.demo.App


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App { println("Row clicked: $it") }
    }
}
