package com.seanproctor.datatable

interface TableDecorationScope {
    val verticalOffsets: List<Int>
    val horizontalOffsets: List<Int>
}
/**
 * Collects the vertical/horizontal offsets of each row/column of a [Table] that are available
 * to a [TableDecoration] when its body is executed on a [TableDecorationChildren] instance.
 */
internal data class TableDecorationScopeImpl(
    override val verticalOffsets: List<Int>,
    override val horizontalOffsets: List<Int>
) : TableDecorationScope
