package com.seanproctor.datatable.material

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.seanproctor.datatable.CellContentProvider

object MaterialCellContentProvider : CellContentProvider {
    @Composable
    override fun RowCellContent(content: @Composable  () -> Unit) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
            content()
        }
    }

    @Composable
    override fun HeaderCellContent(content: @Composable  () -> Unit) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.subtitle2) {
            content()
        }
    }
}