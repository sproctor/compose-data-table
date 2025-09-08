package com.seanproctor.datatable.material3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
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
        isSortIconTrailing: Boolean,
        onClick: (() -> Unit)?,
        content: @Composable () -> Unit
    ) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleSmall) {
            if (onClick != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isSortIconTrailing) {
                        TextButton(onClick = onClick) {
                            content()
                        }
                    }
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
                    if (!isSortIconTrailing) {
                        TextButton(onClick = onClick) {
                            content()
                        }
                    }
                }
            } else {
                content()
            }
        }
    }
}