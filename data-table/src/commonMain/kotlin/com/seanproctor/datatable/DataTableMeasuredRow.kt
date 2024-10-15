package com.seanproctor.datatable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

class DataTableMeasuredRow(
    val placeables: Array<Placeable?>,
    private val key: Any,
    val rowHeight: Int?,
    val columnWidths: Array<Int>,
    val columnAlignment: Array<Alignment>,
    private val tableWidth: Int,
    private val layoutDirection: LayoutDirection,
    override val isHeader: Boolean,
    override val isFooter: Boolean,
    private val logger: ((String) -> Unit)?,
    private val background: @Composable (() -> Unit)
) : DataTableMeasuredElement {
    override val height: Int = rowHeight ?: placeables.filterNotNull().maxOfOrNull { it.height } ?: 0

    private var backgroundOffset: Int = 0

    // optimized for storing x and y offsets for each placeable one by one.
    // array's size == placeables.size * 2, first we store x, then y.
    private val placeableOffsets: IntArray = IntArray(placeables.size * 2)

    override fun position(offset: IntOffset) {
        logger?.invoke("Positioning row at $offset")
        backgroundOffset = offset.y
        var x = offset.x
        placeables.forEachIndexed { index, placeable ->
            if (placeable != null) {
                val columnWidth = columnWidths[index]
                val alignmentOffset = columnAlignment[index].align(
                    size = IntSize(placeable.width, placeable.height),
                    space = IntSize(columnWidth, height),
                    layoutDirection = layoutDirection
                )
                placeableOffsets[index * 2] =
                    x + alignmentOffset.x
                placeableOffsets[index * 2 + 1] = offset.y + alignmentOffset.y
                x += columnWidth
            }
        }
    }

    private fun getOffset(index: Int): IntOffset {
        return IntOffset(placeableOffsets[index * 2], placeableOffsets[index * 2 + 1])
    }

    override fun place(subcomposeScope: SubcomposeMeasureScope, placementBlock: Placeable.PlacementScope) {
        with(placementBlock) {
            with(subcomposeScope) {
                subcompose(key, background).map {
                    it.measure(
                        Constraints(
                            minHeight = height,
                            maxHeight = height,
                            minWidth = tableWidth,
                            maxWidth = tableWidth,
                        )
                    )
                        .place(0, backgroundOffset)
                }
            }
            placeables.forEachIndexed { index, placeable ->
                val offset = getOffset(index)
                placeable?.place(offset)
            }
        }
    }
}

class DataTableMeasuredSimple(
    val placeables: Array<Placeable>,
    private val key: Any,
    override val isHeader: Boolean,
    override val isFooter: Boolean,
    private val tableWidth: Int,
    private val logger: ((String) -> Unit)?,
    private val background: @Composable (() -> Unit)
) : DataTableMeasuredElement {

    override val height: Int = placeables.maxOfOrNull { it.height } ?: 0

    private var offset: IntOffset = IntOffset.Zero

    override fun position(offset: IntOffset) {
        logger?.invoke("Positioning simple at $offset")
        this.offset = offset
    }

    override fun place(subcomposeScope: SubcomposeMeasureScope, placementBlock: Placeable.PlacementScope) {
        with(placementBlock) {
            with(subcomposeScope) {
                subcompose(key, background).map {
                    it.measure(
                        Constraints(
                            minHeight = height,
                            maxHeight = height,
                            minWidth = tableWidth,
                            maxWidth = tableWidth,
                        )
                    )
                        .place(offset)
                }
            }
            placeables.forEach { placeable ->
                placeable.place(offset)
            }
        }
    }
}

interface DataTableMeasuredElement {
    val isHeader: Boolean
    val isFooter: Boolean
    val height: Int

    fun position(offset: IntOffset)

    fun position(x: Int, y: Int) {
        position(IntOffset(x, y))
    }

    fun place(subcomposeScope: SubcomposeMeasureScope, placementBlock: Placeable.PlacementScope)
}