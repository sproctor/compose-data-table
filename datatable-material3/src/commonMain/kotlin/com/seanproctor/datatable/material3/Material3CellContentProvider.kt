package com.seanproctor.datatable.material3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.CellContentProvider
import com.seanproctor.datatable_material3.generated.resources.Res
import com.seanproctor.datatable_material3.generated.resources.arrow_downward
import com.seanproctor.datatable_material3.generated.resources.arrow_upward
import org.jetbrains.compose.resources.painterResource

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
                                Icon(painterResource(Res.drawable.arrow_upward), contentDescription = null)
                            } else {
                                Icon(painterResource(Res.drawable.arrow_downward), contentDescription = null)
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
