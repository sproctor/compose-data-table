package com.seanproctor.datatable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.layout.LazyLayoutIntervalContent
import androidx.compose.foundation.lazy.layout.MutableIntervalList
import androidx.compose.runtime.Composable

@OptIn(ExperimentalFoundationApi::class)
internal class LazyListIntervalContent(
    content: DataTableScope.() -> Unit,
) : LazyLayoutIntervalContent<TableCellInterval>(), DataTableScope {
    override val intervals: MutableIntervalList<TableCellInterval> = MutableIntervalList()

    init {
        apply(content)
    }

    override fun row(onClick: (() -> Unit)?, content: TableRowScope.() -> Unit) {
        TODO("Not yet implemented")
    }
}

@OptIn(ExperimentalFoundationApi::class)
internal class TableCellInterval(
    override val key: ((index: Int) -> Any)?,
    override val type: ((index: Int) -> Any?),
    val item: @Composable LazyItemScope.(index: Int) -> Unit
) : LazyLayoutIntervalContent.Interval