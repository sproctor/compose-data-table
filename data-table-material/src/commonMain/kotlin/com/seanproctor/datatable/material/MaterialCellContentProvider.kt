package com.seanproctor.datatable.material

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Download
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
    override fun HeaderCellContent(sorted: Boolean, sortAscending: Boolean, content: @Composable  () -> Unit) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.subtitle2) {
            println("Sorted: $sorted")
            if (sorted) {
                IconButton(
                    onClick = {}
                ) {
                    if (sortAscending) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null)
                    } else {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null)
                    }
                }
            }
            content()
        }
    }
}