package com.seanproctor.datatable.paging

import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.TableRowScope

internal class PaginatedRowScope(
    private val from: Int,
    private val to: Int,
    private val parentScope: DataTableScope,
) : DataTableScope {
    var index: Int = 0

    override fun row(
        onClick: (() -> Unit)?,
        isHeader: Boolean,
        isFooter: Boolean,
        content: TableRowScope.() -> Unit
    ) {
        if (index in from until to) {
            parentScope.row(onClick, isHeader, isFooter, content)
        }
        index++
    }
}
