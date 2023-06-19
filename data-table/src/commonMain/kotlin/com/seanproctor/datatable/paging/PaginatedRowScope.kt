package com.seanproctor.datatable.paging

import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.TableRowScope

internal class PaginatedRowScope(
    private val from: Int,
    private val to: Int,
    private val parentScope: DataTableScope,
) : DataTableScope {
    var index: Int = 0

    override fun row(onClick: (() -> Unit)?, content: TableRowScope.() -> Unit) {
        if (index in from until to) {
            parentScope.row(onClick, content)
        }
        index++
    }

    override fun rows(count: Int, content: TableRowScope.(Int) -> Unit) {
        TODO("Not yet implemented")
    }
}