package com.seanproctor.datatable.material3

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.seanproctor.datatable.CellContentProvider

object Material3CellContentProvider : CellContentProvider {
    @Composable
    override fun RowCellContent(content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
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
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleSmall) {
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