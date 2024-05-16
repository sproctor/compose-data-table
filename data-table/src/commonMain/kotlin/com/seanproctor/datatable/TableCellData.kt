package com.seanproctor.datatable

import androidx.compose.runtime.Composable

internal data class TableCellData(
    val rowIndex: Int,
    val columnIndex: Int,
    val content: @Composable () -> Unit,
)