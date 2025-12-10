package com.seanproctor.datatable.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue


/**
 * PAGE_SIZE_FIXED_FLAG (-1) indicates dynamic page size.
 * This is only used internally for automatic calculation.
 */
const val PAGE_SIZE_FIXED_FLAG = -1

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
    initialSize: PageSize,
    initialPageIndex: Int = 0,
    initialCount: Int = 0,
    ): PaginatedDataTableState {
    return rememberSaveable(saver = PaginatedDataTableStateImpl.Saver) {
        val pageSizeValue = if (initialSize is PageSize.FixedSize) initialSize.initialPageSize else PAGE_SIZE_FIXED_FLAG
        PaginatedDataTableStateImpl(pageSizeValue, initialPageIndex, initialCount)
    }
}

sealed class PageSize {
    data object FillMaxHeight : PageSize()
    data class FixedSize(val initialPageSize: Int) : PageSize()
}

