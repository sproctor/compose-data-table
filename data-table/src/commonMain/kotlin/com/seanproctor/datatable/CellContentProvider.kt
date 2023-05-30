package com.seanproctor.datatable

import androidx.compose.runtime.Composable

interface CellContentProvider {
    @Composable
    fun RowCellContent(content: @Composable () -> Unit)

    @Composable
    fun HeaderCellContent(sorted: Boolean, sortAscending: Boolean, content: @Composable () -> Unit)
}

object DefaultCellContentProvider : CellContentProvider {

    @Composable
    override fun RowCellContent(content: @Composable () -> Unit) {
        content()
    }

    @Composable
    override fun HeaderCellContent(sorted: Boolean, sortAscending: Boolean, content: @Composable () -> Unit) {
        content()
    }
}