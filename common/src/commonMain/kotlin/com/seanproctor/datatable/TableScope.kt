package com.seanproctor.datatable

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

/**
 * Collects information about the children of a [Table] when
 * its body is executed with a [TableScope] as argument.
 */
@LayoutScopeMarker
@Immutable
interface TableScope {
    /**
     * Creates a header row
     */
    fun headerRow(content: TableRowScope.() -> Unit)

    /**
     * Creates a new row in the [Table] with the specified content.
     */
    fun row(content: TableRowScope.() -> Unit)

    /**
     * Adds a decoration which will be placed either above or below the content of the [Table].
     * This can be either a component, such as Layout or SizedRectangle, or a Draw composable.
     * Note that decorations are measured with tight constraints to fill the size of the [Table],
     * and the offsets of each row and column of the [Table] are available inside the body of this.
     *
     * Example usage:
     *
     * @sample androidx.ui.layout.samples.TableWithDecorations
     *
     * @param overlay Whether the decoration is placed above (true) or below (false) the content.
     */
    fun decoration(overlay: Boolean, content: @Composable TableDecorationScope.() -> Unit)
}

internal class TableScopeImpl : TableScope {
    val tableChildren = mutableListOf<TableRowScope.() -> Unit>()
    val tableDecorationsOverlay = mutableListOf<@Composable TableDecorationScope.() -> Unit>()
    val tableDecorationsUnderlay = mutableListOf<@Composable TableDecorationScope.() -> Unit>()
    var tableHeader: (TableRowScope.() -> Unit)? = null

    override fun headerRow(content: TableRowScope.() -> Unit) {
        tableHeader = content
    }

    override fun row(content: TableRowScope.() -> Unit) {
        tableChildren += content
    }

    /**
     * Adds a decoration which will be placed either above or below the content of the [Table].
     * This can be either a component, such as Layout or SizedRectangle, or a Draw composable.
     * Note that decorations are measured with tight constraints to fill the size of the [Table],
     * and the offsets of each row and column of the [Table] are available inside the body of this.
     *
     * @param overlay Whether the decoration is placed above (true) or below (false) the content.
     */
    override fun decoration(overlay: Boolean, content: @Composable TableDecorationScope.() -> Unit) {
        if (overlay) {
            tableDecorationsOverlay += content
        } else {
            tableDecorationsUnderlay += content
        }
    }
}
