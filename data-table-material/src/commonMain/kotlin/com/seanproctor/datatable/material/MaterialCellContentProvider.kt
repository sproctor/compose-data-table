package com.seanproctor.datatable.material

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.seanproctor.datatable.CellContentProvider

object MaterialCellContentProvider : CellContentProvider {
    @Composable
    override fun RowCellContent(content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
            content()
        }
    }

    @Composable
    override fun HeaderCellContent(
        sorted: Boolean,
        sortAscending: Boolean,
        onClick: (() -> Unit)?,
        content: @Composable () -> Unit
    ) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.subtitle2) {
            if (onClick != null) {
                if (sorted) {
                    IconButton(
                        onClick = onClick
                    ) {
                        if (sortAscending) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null)
                        } else {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null)
                        }
                    }
                }
                TextButton(onClick = onClick) {
                    content()
                }
            } else {
                content()
            }
        }
    }

    @Composable
    override fun CheckboxCellContent(checked: Boolean, onCheckChanged: (Boolean) -> Unit) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckChanged
        )
    }
}