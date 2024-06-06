package com.seanproctor.datatable

internal class TableRowData(
    val onClick: ((index: Int) -> Unit)?,
    val key: ((index: Int) -> Any)?,
    val item: TableRowScope.(index: Int) -> Unit,
) {

}
