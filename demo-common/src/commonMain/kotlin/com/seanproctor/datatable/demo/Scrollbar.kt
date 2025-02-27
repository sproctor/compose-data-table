package com.seanproctor.datatable.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.seanproctor.datatable.DataTableScrollState
import io.github.oikvpqya.compose.fastscroller.ScrollbarAdapter
import kotlin.math.roundToInt

@Composable
fun rememberScrollbarAdapter(scrollState: DataTableScrollState): ScrollbarAdapter {
    return remember(scrollState) { DataTableScrollbarAdapter(scrollState) }
}

internal class DataTableScrollbarAdapter(
    private val scrollState: DataTableScrollState
) : ScrollbarAdapter {
    override val scrollOffset: Double get() = scrollState.offset.toDouble()

    override suspend fun scrollTo(scrollOffset: Double) {
        scrollState.scrollTo(scrollOffset.roundToInt())
    }

    override val contentSize: Double
        // This isn't strictly correct, as the actual content can be smaller
        // than the viewport when scrollState.maxValue is 0, but the scrollbar
        // doesn't really care as long as contentSize <= viewportSize; it's
        // just not showing itself
        get() = scrollState.totalSize.toDouble()

    override val viewportSize: Double
        get() = scrollState.viewportSize.toDouble()
}
