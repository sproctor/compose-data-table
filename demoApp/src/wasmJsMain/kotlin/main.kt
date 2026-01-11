import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.seanproctor.datatable.demo.App
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        App { println("Row clicked: $it") }
    }
}
