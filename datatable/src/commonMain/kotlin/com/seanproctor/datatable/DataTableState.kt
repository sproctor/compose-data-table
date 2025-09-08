package com.seanproctor.datatable

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun rememberDataTableState(): DataTableState {
    return remember { DataTableState() }
}

@Stable
class DataTableState {
    val verticalScrollState: DataTableScrollState = DataTableScrollState()
    val horizontalScrollState: DataTableScrollState = DataTableScrollState()
}

@Stable
class DataTableScrollState : ScrollableState {

    // TODO: This really needs improvement
    var offset: Int by mutableIntStateOf(0)
        private set

    var viewportSize: Int by mutableIntStateOf(0)
        internal set

    var totalSize: Int by mutableIntStateOf(0)
        internal set

    /**
     * Provides a modifier which allows to delay some interactions (e.g. scroll)
     * until layout is ready.
     */
    internal val awaitLayoutModifier = AwaitFirstLayoutModifier()

    /**
     * The amount of scroll to be consumed in the next layout pass.  Scrolling forward is negative
     * - that is, it is the amount that the items are offset in y
     */
    private var scrollToBeConsumed = 0f

    override val canScrollForward: Boolean
        get() = offset > 0
    override val canScrollBackward: Boolean
        get() = offset < totalSize - viewportSize

    /**
     * The ScrollableController instance. We keep it as we need to call stopAnimation on it once
     * we reached the end of the list.
     */
    private val scrollableState = ScrollableState { -onScroll(-it) }

    override val isScrollInProgress: Boolean
        get() = scrollableState.isScrollInProgress

    /**
     * [InteractionSource] that will be used to dispatch drag events when this
     * list is being dragged. If you want to know whether the fling (or animated scroll) is in
     * progress, use [isScrollInProgress].
     */
    val interactionSource: InteractionSource get() = internalInteractionSource

    internal val internalInteractionSource: MutableInteractionSource = MutableInteractionSource()

    override fun dispatchRawDelta(delta: Float): Float =
        scrollableState.dispatchRawDelta(delta)

    /**
     * Call this function to take control of scrolling and gain the ability to send scroll events
     * via [ScrollScope.scrollBy]. All actions that change the logical scroll position must be
     * performed within a [scroll] block (even if they don't call any other methods on this
     * object) in order to guarantee that mutual exclusion is enforced.
     *
     * If [scroll] is called from elsewhere, this will be canceled.
     */
    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        awaitLayoutModifier.waitForFirstLayout()
        scrollableState.scroll(scrollPriority, block)
    }

    /**
     * Instantly jump to the given position in pixels.
     *
     * Cancels the currently running scroll, if any, and suspends until the cancellation is
     * complete.
     *
     * @see animateScrollTo for an animated version
     *
     * @param value number of pixels to scroll by
     * @return the amount of scroll consumed
     */
    suspend fun scrollTo(value: Int): Float = this.scrollBy((this.offset - value).toFloat())

    internal fun onScroll(distance: Float): Float {
        if (distance < 0f && !canScrollForward || distance > 0f && !canScrollBackward) {
            return 0f
        }
        check(abs(scrollToBeConsumed) <= 0.5f) {
            "entered drag with non-zero pending scroll: $scrollToBeConsumed"
        }
        scrollToBeConsumed += distance

        // scrollToBeConsumed will be consumed synchronously during the forceRemeasure invocation
        // inside measuring we do scrollToBeConsumed.roundToInt() so there will be no scroll if
        // we have less than 0.5 pixels
        if (abs(scrollToBeConsumed) > 0.5f) {
            if (distance > 0f) {
                val remainingSpace = totalSize - viewportSize - offset
                val pixelsScrolled = min(scrollToBeConsumed.roundToInt(), remainingSpace)
                scrollToBeConsumed -= pixelsScrolled
                offset += pixelsScrolled
            } else {
                val pixelsScrolled = max(scrollToBeConsumed.roundToInt(), -offset)
                scrollToBeConsumed -= pixelsScrolled
                offset += pixelsScrolled
            }
        }

        // here scrollToBeConsumed is already consumed during the forceRemeasure invocation
        if (abs(scrollToBeConsumed) <= 0.5f) {
            // We consumed all of it - we'll hold onto the fractional scroll for later, so report
            // that we consumed the whole thing
            return distance
        } else {
            val scrollConsumed = distance - scrollToBeConsumed
            // We did not consume all of it - return the rest to be consumed elsewhere (e.g.,
            // nested scrolling)
            scrollToBeConsumed = 0f // We're not consuming the rest, give it back
            return scrollConsumed
        }
    }
}
