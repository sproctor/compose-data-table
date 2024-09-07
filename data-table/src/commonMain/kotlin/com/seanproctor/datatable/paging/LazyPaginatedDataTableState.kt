package com.seanproctor.datatable.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class LazyPaginatedDataTableState(
    pageSize: Int,
    pageIndex: Int,
    count: Int
): PaginatedDataTableState(pageSize, pageIndex) {

    override var count by mutableStateOf(count)

    companion object {
        val Saver: Saver<LazyPaginatedDataTableState, *> = listSaver(
            save = { listOf(it.pageSize, it.pageIndex, it.count) },
            restore = {
                LazyPaginatedDataTableState(it[0], it[1], it[2])
            }
        )
    }
}

@Composable
fun rememberLazyPaginatedDataTableState(
    initialPageSize: Int,
    totalItems: Int,
    initialPageIndex: Int = 0,
): LazyPaginatedDataTableState {
    return rememberSaveable(saver = LazyPaginatedDataTableState.Saver) {
        LazyPaginatedDataTableState(initialPageSize, initialPageIndex, totalItems)
    }
}