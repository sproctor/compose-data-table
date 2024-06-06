package com.seanproctor.datatable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.IntervalList
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.foundation.lazy.layout.getDefaultLazyLayoutKey
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@ExperimentalFoundationApi
internal class DataTableItemProvider(
    private val intervals: IntervalList<TableCellData>
) : LazyLayoutItemProvider {

    private val keyMap: Map<Any, Int> =
        buildMap {
            repeat(intervals.size) { index ->
                val data = intervals[index].value
                data.key?.invoke(data.rowIndex, data.columnIndex)?.let { key ->
                    put(key, index)
                }
            }
        }

    override val itemCount: Int
        get() = intervals.size

    @Composable
    override fun Item(index: Int, key: Any) {
        val data = intervals[index].value
        data.content(TableCellScopeImpl(data.rowIndex, data.columnIndex))
    }

    override fun getIndex(key: Any): Int {
        return keyMap.getOrElse(key) { -1 }
    }

    override fun getKey(index: Int): Any {
        val data = intervals[index].value
        return data.key?.invoke(data.rowIndex, data.columnIndex) ?: getDefaultLazyLayoutKey(index)
    }
}

@ExperimentalFoundationApi
@Composable
internal fun rememberDataTableItemProviderLambda(
    content: DataTableScope.() -> Unit
): () -> DataTableItemProvider {
    val latestContent = rememberUpdatedState(content)
    return remember {
        val scope = DataTableScopeImpl().apply(latestContent)
    }
}