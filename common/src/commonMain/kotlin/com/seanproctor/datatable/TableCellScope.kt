package com.seanproctor.datatable

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.ui.Modifier

@LayoutScopeMarker
interface TableCellScope {
    val rowIndex: Int
    val columnIndex: Int

    fun Modifier.tableCell() = this.then(
        TableParentData(rowIndex, columnIndex)
    )
}

internal data class TableCellScopeImpl(
    override val rowIndex: Int,
    override val columnIndex: Int
) : TableCellScope
