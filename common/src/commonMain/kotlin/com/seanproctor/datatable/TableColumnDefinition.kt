package com.seanproctor.datatable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

data class TableColumnDefinition(
    val alignment: Alignment = Alignment.CenterStart,
    val width: TableColumnWidth = TableColumnWidth.Flex(1f),
    val header: @Composable TableCellScope.() -> Unit,
)
