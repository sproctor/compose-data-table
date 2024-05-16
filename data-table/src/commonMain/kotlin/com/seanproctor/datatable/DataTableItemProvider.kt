package com.seanproctor.datatable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.IntervalList
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable

@ExperimentalFoundationApi
private class DataTableItemProvider(
    private val intervals: IntervalList<TableCellData>
) : LazyLayoutItemProvider {
    override val itemCount: Int
        get() = intervals.size

    @Composable
    override fun Item(index: Int, key: Any) {
        intervals[index].value.content()
    }

    override fun getContentType(index: Int): Any? {
        return super.getContentType(index)
    }

    override fun getIndex(key: Any): Int {
        return super.getIndex(key)
    }

    override fun getKey(index: Int): Any {
        return super.getKey(index)
    }

    private fun getItem(index: Int): TableCellData {
        var position = index
        intervals.forEach { interval ->
            if (position < interval.size) {
                return@forEach interval.
            }
            position -= interval.size
        }
    }
}