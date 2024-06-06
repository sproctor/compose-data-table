package com.seanproctor.datatable

import androidx.compose.runtime.Composable

internal data class TableCellData(
    val rowIndex: Int,
    val columnIndex: Int,
    val key: ((Int, Int) -> Any)?,
    val content: @Composable TableCellScope.() -> Unit,
)