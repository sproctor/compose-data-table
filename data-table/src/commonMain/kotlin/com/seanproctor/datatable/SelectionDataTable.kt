package com.seanproctor.datatable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SelectionDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable (rowIndex: Int) -> Unit = { },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    horizontalPadding: Dp = 16.dp,
    footer: @Composable () -> Unit = { },
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    showCheckboxColumn: Boolean = false,
    selectedRows: Set<Int> = emptySet(),
    onSelectAll: (Boolean) -> Unit = {},
    onRowSelected: (Int, Boolean) -> Unit = { _, _ -> },
    content: DataTableScope.() -> Unit
) {
    
}