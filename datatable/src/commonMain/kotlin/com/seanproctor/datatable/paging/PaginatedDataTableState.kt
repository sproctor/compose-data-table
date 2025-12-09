package com.seanproctor.datatable.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

interface PaginatedDataTableState {
    var pageSize: Int
    var pageIndex: Int
    var count: Int
}

@Stable
private class PaginatedDataTableStateImpl (
    pageSize: Int,
    pageIndex: Int,
    count: Int,
) : PaginatedDataTableState {
    override var pageSize by mutableStateOf(pageSize)
    override var pageIndex by mutableStateOf(pageIndex)
    override var count by mutableStateOf(count)

    companion object {
        val Saver: Saver<PaginatedDataTableState, *> = listSaver(
            save = { listOf(it.pageSize, it.pageIndex, it.count) },
            restore = {
                PaginatedDataTableStateImpl(it[0], it[1], it[2])
            }
        )
    }
}

@Composable
fun rememberPaginatedDataTableState(
    initialPageSize: Int,
    initialPageIndex: Int = 0,
    initialCount: Int = 0,

): PaginatedDataTableState {
    return rememberSaveable(saver = PaginatedDataTableStateImpl.Saver) {
        PaginatedDataTableStateImpl(initialPageSize, initialPageIndex, initialCount)
    }
}

sealed class SizePage {
    data object FillMaxHeight : SizePage()
    data class FixedSize(val initialPageSize: Int) : SizePage()
}
