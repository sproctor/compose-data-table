package com.seanproctor.datatable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

data class DataColumn(
    val alignment: Alignment = Alignment.CenterStart,
    val width: TableColumnWidth = TableColumnWidth.Flex(1f),
    val onSort: ((columnIndex: Int, ascending: Boolean) -> Unit)? = null,
    val isSortIconTrailing: Boolean = true,
    val header: @Composable () -> Unit,
)
