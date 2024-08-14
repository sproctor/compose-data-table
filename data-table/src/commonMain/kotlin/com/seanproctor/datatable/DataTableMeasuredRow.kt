package com.seanproctor.datatable

import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection

class DataTableMeasuredRow(
    val placeables: Array<Placeable?>,
    val columnWidths: Array<Int>,
    val columnAlignment: Array<Alignment.Horizontal>,
    private val layoutDirection: LayoutDirection,
    override val isHeader: Boolean,
    override val isFooter: Boolean,
    private val logger: ((String) -> Unit)?
) : DataTableMeasuredElement {
    override val height: Int = placeables.filterNotNull().maxOfOrNull { it.height } ?: 0

    var background: Placeable? = null

    private var backgroundOffset: Int = 0

    // optimized for storing x and y offsets for each placeable one by one.
    // array's size == placeables.size * 2, first we store x, then y.
    private val placeableOffsets: IntArray = IntArray(placeables.size * 2)

    override fun position(offset: Int) {
        logger?.invoke("Positioning row at $offset")
        backgroundOffset = offset
        var x = 0
        placeables.forEachIndexed { index, placeable ->
            if (placeable != null) {
                val columnWidth = columnWidths[index]
                placeableOffsets[index * 2] =
                    x + columnAlignment[index].align(placeable.width, columnWidth, layoutDirection)
                placeableOffsets[index * 2 + 1] = offset
                x += columnWidth
            }
        }
    }

    private fun getOffset(index: Int): IntOffset {
        return IntOffset(placeableOffsets[index * 2], placeableOffsets[index * 2 + 1])
    }

    override fun place(scope: Placeable.PlacementScope) = with(scope) {
        background?.place(0, backgroundOffset)
        placeables.forEachIndexed { index, placeable ->
            val offset = getOffset(index)
            placeable?.place(offset)
        }
    }
}

class DataTableMeasuredSimple(
    val placeables: Array<Placeable>,
    override val isHeader: Boolean,
    override val isFooter: Boolean,
    private val logger: ((String) -> Unit)?
) : DataTableMeasuredElement {

    var background: Placeable? = null

    override val height: Int = placeables.maxOfOrNull { it.height } ?: 0

    private var offset: Int = 0

    override fun position(offset: Int) {
        logger?.invoke("Positioning simple at $offset")
        this.offset = offset
    }

    override fun place(scope: Placeable.PlacementScope) = with(scope) {
        background?.place(0, offset)
        placeables.forEachIndexed { index, placeable ->
            placeable.place(0, offset)
        }
    }
}

interface DataTableMeasuredElement {
    val isHeader: Boolean
    val isFooter: Boolean
    val height: Int

    fun position(offset: Int)

    fun place(scope: Placeable.PlacementScope)
}