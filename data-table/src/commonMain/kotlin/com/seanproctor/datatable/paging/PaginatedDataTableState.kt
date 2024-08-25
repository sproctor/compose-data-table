package com.seanproctor.datatable.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class PaginatedDataTableState(
    pageSize: Int,
    pageIndex: Int,
) {
    var pageSize by mutableStateOf(pageSize)
    var pageIndex by mutableStateOf(pageIndex)
    var count by mutableStateOf(0)

    companion object {
        val Saver: Saver<PaginatedDataTableState, *> = listSaver(
            save = { listOf(it.pageSize, it.pageIndex) },
            restore = {
                PaginatedDataTableState(it[0], it[1])
            }
        )
    }
}

@Composable
fun rememberPaginatedDataTableState(
    initialPageSize: Int,
    initialPageIndex: Int = 0,
): PaginatedDataTableState {
    return rememberSaveable(saver = PaginatedDataTableState.Saver) {
        PaginatedDataTableState(initialPageSize, initialPageIndex)
    }
}
