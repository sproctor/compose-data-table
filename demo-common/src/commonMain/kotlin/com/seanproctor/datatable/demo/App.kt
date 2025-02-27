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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.material3.DataTable
import com.seanproctor.datatable.material3.LazyPaginatedDataTable
import com.seanproctor.datatable.material3.PaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import io.github.oikvpqya.compose.fastscroller.HorizontalScrollbar
import io.github.oikvpqya.compose.fastscroller.VerticalScrollbar
import io.github.oikvpqya.compose.fastscroller.material3.defaultMaterialScrollbarStyle
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(onRowClick: (Int) -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        Column {
            var selectedIndex by remember { mutableStateOf(0) }
            PrimaryTabRow(
                selectedTabIndex = selectedIndex
            ) {
                val titles = listOf("Normal", "Paginated", "Lazy paginated")
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
            var sortColumnIndex by remember { mutableStateOf<Int?>(null) }
            var sortAscending by remember { mutableStateOf(true) }

            val sortedData = when (sortColumnIndex) {
                null -> rowData
                1 -> if (sortAscending) rowData.sortedBy { it.text } else rowData.sortedByDescending { it.text }
                2 -> if (sortAscending) rowData.sortedBy { it.value } else rowData.sortedByDescending { it.value }
                else -> throw IllegalStateException("Invalid column index")
            }

            val columns = listOf(
                DataColumn(width = TableColumnWidth.Wrap) { },
                DataColumn(
                    width = TableColumnWidth.Wrap,
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
            if (selectedIndex == 0) {
                Box {
                    DataTable(
                        columns = columns,
                        state = scrollState,
                        sortColumnIndex = sortColumnIndex,
                        sortAscending = sortAscending,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
//                        headerBackgroundColor = MaterialTheme.colorScheme.surface,
//                        footerBackgroundColor = MaterialTheme.colorScheme.surface,
                        footer = {
                            Box {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    text = "Footer",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
//                    logger = { println(it) }
                    ) {
                        generateTable(
                            colorEven = colorEven,
                            colorOdd = colorOdd,
                            data = sortedData,
                            onRowClick = onRowClick,
                        )
                    }
                    LaunchedEffect(scrollState.horizontalScrollState.viewportSize) {
                        println("viewport: ${scrollState.horizontalScrollState.viewportSize}")
                        println("total size: ${scrollState.horizontalScrollState.totalSize}")
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
            } else if (selectedIndex == 1) {
                PaginatedDataTable(
                    columns = columns,
                    state = rememberPaginatedDataTableState(5),
                    sortColumnIndex = sortColumnIndex,
                    sortAscending = sortAscending,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    logger = { println(it) }
                ) {
                    generateTable(
                        colorEven = colorEven,
                        colorOdd = colorOdd,
                        data = sortedData,
                        onRowClick = onRowClick,
                    )
                }
            } else {
                LazyPaginatedDataTable(
                    columns = columns,
                    state = rememberPaginatedDataTableState(initialPageSize = 5, initialCount = sortedData.size),
                    sortColumnIndex = sortColumnIndex,
                    sortAscending = sortAscending,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    logger = { println(it) },
                    fetchPage = { state ->
                        val fromIndex = state.pageIndex * state.pageSize
                        val toIndex = min(fromIndex + state.pageSize, sortedData.size)
                        val subset = sortedData.subList(fromIndex, toIndex)
                        subset.forEachIndexed { index, rowData ->
                            row {
                                onClick = { onRowClick(fromIndex + index) }
                                cell { }
                                cell {
                                    Text(rowData.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                cell {
                                    Text(rowData.value.toString())
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

fun DataTableScope.generateTable(
    colorEven: Color,
    colorOdd: Color,
    data: List<DemoData>,
    onRowClick: (Int) -> Unit,
) {
    data.forEachIndexed { index, rowData ->
        row {
            backgroundColor = if (index % 2 == 0) colorEven else colorOdd
            onClick = { onRowClick(index) }
            cell { }
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
