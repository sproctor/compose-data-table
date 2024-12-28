package com.seanproctor.datatable.demo

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.seanproctor.datatable.DataTableScrollState
import kotlin.math.roundToInt

@Composable
actual fun VerticalScrollbar(scrollState: DataTableScrollState, modifier: Modifier) {
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = modifier,
    )
}

@Composable
actual fun HorizontalScrollbar(scrollState: DataTableScrollState, modifier: Modifier) {
    HorizontalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = modifier,
    )
}

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