package com.seanproctor.datatable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@DslMarker
annotation class DataTableScopeMarker

/**
 * Collects information about the children of a [BasicDataTable] when
 * its body is executed with a [DataTableScope] as argument.
 */
@DataTableScopeMarker
interface DataTableScope {
    /**
     * Creates a new row in the [BasicDataTable] with the specified content.
     */
    fun row(
        content: TableRowScope.() -> Unit
    )
}

@DataTableScopeMarker
interface TableRowScope {
    var onClick: (() -> Unit)?
    var height: Dp
    var isHeader: Boolean
    var isFooter: Boolean
    var color: Color

    fun cell(content: @Composable () -> Unit)
}

internal class DataTableScopeImpl(
    content: DataTableScope.() -> Unit
) : DataTableScope {
    val tableRows = mutableListOf<TableRowScopeImpl>()

    init {
        apply(content)
    }

    override fun row(
        content: TableRowScope.() -> Unit
    ) {
        tableRows += TableRowScopeImpl(content)
    }
}

internal class TableRowScopeImpl(
    content: TableRowScope.() -> Unit
) : TableRowScope {
    override var onClick: (() -> Unit)? = null
    override var height: Dp = Dp.Unspecified
    override var isHeader: Boolean = false
    override var isFooter: Boolean = false
    override var color: Color = Color.Unspecified
    val cells = mutableListOf<@Composable () -> Unit>()

    init {
        apply(content)
    }

    override fun cell(content: @Composable () -> Unit) {
        cells += content
    }
}
