package com.seanproctor.datatable.paging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.*

@Composable
fun BasicPaginatedDataTable(
    columns: List<DataColumn>,
    state: PaginatedDataTableState,
    modifier: Modifier = Modifier,
    separator: @Composable () -> Unit = { },
    headerHeight: Dp,
    rowHeight: Dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headerBackgroundColor: Color = Color.Unspecified,
    footerBackgroundColor: Color = Color.Unspecified,
    footer: @Composable (Int) -> Unit = { },
    cellContentProvider: CellContentProvider = DefaultCellContentProvider,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.() -> Unit
) {
    var pageSize by remember { mutableStateOf(state.pageSize) }
    var calculated by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                val heightPx = coords.size.height.toFloat()

                val rowHeightPx = with(density) { rowHeight.toPx() }

                val rows = (heightPx / rowHeightPx).toInt()
                pageSize = rows - 3
                println("pageRows is: $rows")
                println("pageSize is: $pageSize")
            }
    ) {
        BasicDataTable(
            columns = columns,
            modifier = modifier
    //            .onGloballyPositioned { coords ->
    //                if (!calculated) {
    //                    val tableHeight = coords.size.height.dp
    //                    val pageRows = ((tableHeight - rowHeight) / rowHeight).toInt()
    //                    pageSize = pageRows - 1
    //                    println("pageRows is: $pageRows")
    //                    println("pageSize is: $pageSize")
    //                    calculated = true
    //                }
    //            },
                    ,
            state = remember(pageSize, state.pageIndex) { DataTableState() },
            separator = separator,
            headerHeight = headerHeight,
            rowHeight = rowHeight,
            contentPadding = contentPadding,
            headerBackgroundColor = headerBackgroundColor,
            footerBackgroundColor = footerBackgroundColor,
            footer = { footer(pageSize) },
            cellContentProvider = cellContentProvider,
            sortColumnIndex = sortColumnIndex,
            sortAscending = sortAscending,
            logger = logger,
        ) {
            val start = state.pageIndex * pageSize
            val scope = PaginatedRowScope(start, start + pageSize, this)
            content(scope)
            if (state.count != scope.index) {
                state.count = scope.index
            }
        }
    }
}
