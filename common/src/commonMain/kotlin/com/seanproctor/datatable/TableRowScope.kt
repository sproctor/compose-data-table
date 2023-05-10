package com.seanproctor.datatable

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable

@LayoutScopeMarker
interface TableRowScope {
    val rowIndex: Int

    fun cell(content: @Composable TableCellScope.() -> Unit)
}

internal data class TableRowScopeImpl(override val rowIndex: Int) : TableRowScope {
    val cells = mutableListOf<@Composable TableCellScope.() -> Unit>()

    override fun cell(content: @Composable TableCellScope.() -> Unit) {
        cells += content
    }
}
