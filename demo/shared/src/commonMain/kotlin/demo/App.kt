package com.seanproctor.datatable.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.material3.DataTable
import com.seanproctor.datatable.material3.PaginatedDataTable
import com.seanproctor.datatable.paging.PageSize
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import io.github.oikvpqya.compose.fastscroller.HorizontalScrollbar
import io.github.oikvpqya.compose.fastscroller.VerticalScrollbar
import io.github.oikvpqya.compose.fastscroller.material3.defaultMaterialScrollbarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(onRowClick: (Int) -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        Column {
            var selectedIndex by remember { mutableStateOf(0) }
            PrimaryTabRow(
                selectedTabIndex = selectedIndex
            ) {
                val titles = listOf("Normal", "Paginated")
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index },
                        text = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }
            val rowData = (0 until 30).map { index ->
                DemoData(index + 1f, "index: $index")
            }
            println("rowData: $rowData")
            var sortColumnIndex by remember { mutableStateOf<Int?>(null) }
            var sortAscending by remember { mutableStateOf(true) }
            var selection by remember { mutableStateOf(emptySet<DemoData>()) }
            val toggleSelection: (DemoData, Boolean) -> Unit = { data, isSelected ->
                selection = if (isSelected) selection + data else selection - data
            }

            val sortedData = when (sortColumnIndex) {
                null -> rowData
                0 -> if (sortAscending) rowData.sortedBy { it.text } else rowData.sortedByDescending { it.text }
                1 -> if (sortAscending) rowData.sortedBy { it.value } else rowData.sortedByDescending { it.value }
                else -> throw IllegalStateException("Invalid column index")
            }

            val columns = listOf(
                DataColumn(
                    //width = TableColumnWidth.Wrap,
                    onSort = { index, ascending ->
                        sortColumnIndex = index
                        sortAscending = ascending
                    }
                ) {
                    Text("Column1")
                },
                DataColumn(
                    alignment = Alignment.CenterEnd,
                    isSortIconTrailing = false,
                    onSort = { index, ascending ->
                        sortColumnIndex = index
                        sortAscending = ascending
                    }
                ) {
                    Text("Column2")
                }
            )

            val colorEven = MaterialTheme.colorScheme.surfaceBright
            val colorOdd = MaterialTheme.colorScheme.surfaceDim
            val scrollState = remember(selectedIndex) { DataTableState() }
            when (selectedIndex) {
                0 -> {
                    Box {
                        DataTable(
                            columns = columns,
                            state = scrollState,
                            sortColumnIndex = sortColumnIndex,
                            sortAscending = sortAscending,
                            selectable = true,
                            onSelectAllChange = { selection = if (it) sortedData.toSet() else emptySet() },
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            footer = {
                                Box {
                                    Text(
                                        modifier = Modifier.align(Alignment.CenterEnd),
                                        text = "${selection.size} selected",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        ) {
                            generateTable(
                                colorEven = colorEven,
                                colorOdd = colorOdd,
                                data = sortedData,
                                selection = selection,
                                toggleSelection = toggleSelection,
                            )
                        }
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(scrollState.verticalScrollState),
                            style = defaultMaterialScrollbarStyle(),
                            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                        )
                        HorizontalScrollbar(
                            adapter = rememberScrollbarAdapter(scrollState.horizontalScrollState),
                            style = defaultMaterialScrollbarStyle(),
                            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                        )
                    }
                }
                1 -> {
                    PaginatedDataTable(
                        columns = columns,
                        state = rememberPaginatedDataTableState(count = sortedData.size, pageSize = PageSize.FitHeight),
                        sortColumnIndex = sortColumnIndex,
                        sortAscending = sortAscending,
                        selectable = true,
                        // Selection spans pages, so the header reflects the whole data set.
                        selectAllState = when (selection.size) {
                            0 -> ToggleableState.Off
                            sortedData.size -> ToggleableState.On
                            else -> ToggleableState.Indeterminate
                        },
                        onSelectAllChange = { selection = if (it) sortedData.toSet() else emptySet() },
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        logger = { println(it) }
                    ) { fromIndex, toIndex ->
                        val subset = sortedData.subList(fromIndex, toIndex)
                        subset.forEachIndexed { index, rowData ->
                            row {
                                onClick = { onRowClick(fromIndex + index) }
                                backgroundColor = if (index % 2 == 0) colorEven else colorOdd
                                selected = rowData in selection
                                onSelectedChange = { toggleSelection(rowData, it) }
                                cell {
                                    Text(rowData.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                cell {
                                    Text(rowData.value.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DataTableScope.generateTable(
    colorEven: Color,
    colorOdd: Color,
    data: List<DemoData>,
    selection: Set<DemoData>,
    toggleSelection: (DemoData, Boolean) -> Unit,
) {
    data.forEachIndexed { index, rowData ->
        row {
            backgroundColor = if (index % 2 == 0) colorEven else colorOdd
            // No onClick, so clicking the row toggles its selection.
            selected = rowData in selection
            onSelectedChange = { toggleSelection(rowData, it) }
            cell {
                Text(rowData.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            cell {
                Text(rowData.value.toString())
            }
        }
    }
}

data class DemoData(val value: Float, val text: String)
