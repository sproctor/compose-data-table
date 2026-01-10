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
    val count: Int
    val pageSize: PageSize
    var currentPageIndex: Int
    var currentPageSize: Int
}

@Stable
private class PaginatedDataTableStateImpl(
    override val count: Int,
    override val pageSize: PageSize,
    initialPageIndex: Int,
) : PaginatedDataTableState {
    override var currentPageSize: Int by mutableStateOf(
        when (pageSize) {
            PageSize.FitHeight -> 1
            is PageSize.FixedSize -> pageSize.size
        }
    )
    override var currentPageIndex by mutableStateOf(initialPageIndex)

    companion object {
        val Saver: Saver<PaginatedDataTableState, *> = listSaver(
            save = { listOf(it.pageSize, it.currentPageIndex, it.count, it.currentPageSize) },
            restore = {
                PaginatedDataTableStateImpl(
                    pageSize = it[0] as PageSize,
                    initialPageIndex = it[1] as Int,
                    count = it[2] as Int,
                ).apply {
                    currentPageSize = it[3] as Int
                }
            }
        )
    }
}

@Composable
fun rememberPaginatedDataTableState(
    count: Int,
    pageSize: PageSize,
    initialPageIndex: Int = 0,
): PaginatedDataTableState {
    return rememberSaveable(pageSize, count, initialPageIndex, saver = PaginatedDataTableStateImpl.Saver) {
        PaginatedDataTableStateImpl(
            count = count,
            pageSize = pageSize,
            initialPageIndex = initialPageIndex,
        )
    }
}

sealed class PageSize {
    data object FitHeight : PageSize()
    data class FixedSize(val size: Int) : PageSize()
}

