package com.seanproctor.datatable.material3

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.seanproctor.datatable.CellContentProvider

object Material3CellContentProvider : CellContentProvider {
    @Composable
    override fun RowCellContent(content: @Composable  () -> Unit) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
            content()
        }
    }

    @Composable
    override fun HeaderCellContent(sorted: Boolean, sortAscending: Boolean, content: @Composable  () -> Unit) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleSmall) {
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