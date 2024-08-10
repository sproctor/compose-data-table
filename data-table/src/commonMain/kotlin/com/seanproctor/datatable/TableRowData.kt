package com.seanproctor.datatable

internal data class TableRowData(
    val onClick: (() -> Unit)?,
    val isHeader: Boolean,
    val isFooter: Boolean,
    val content: TableRowScope.() -> Unit,
)
