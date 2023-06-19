package com.seanproctor.datatable

internal data class TableRowData(
    val onClick: (() -> Unit)?,
    val content: TableRowScope.() -> Unit,
)
