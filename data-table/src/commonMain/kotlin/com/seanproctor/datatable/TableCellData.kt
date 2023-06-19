package com.seanproctor.datatable

import androidx.compose.runtime.Composable

internal data class TableCellData(
    val content: @Composable TableCellScope.() -> Unit,
)