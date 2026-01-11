
import androidx.compose.ui.window.ComposeUIViewController
import com.seanproctor.datatable.demo.App
import platform.UIKit.UIViewController

@Suppress("standard:function-naming")
fun MainViewController(): UIViewController = ComposeUIViewController {
  App { println("Row clicked: $it") }
}
