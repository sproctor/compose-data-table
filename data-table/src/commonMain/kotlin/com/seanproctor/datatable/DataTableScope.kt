/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seanproctor.datatable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.lazy.layout.IntervalList
import androidx.compose.foundation.lazy.layout.MutableIntervalList
import androidx.compose.runtime.Immutable

/**
 * Collects information about the children of a [BasicDataTable] when
 * its body is executed with a [DataTableScope] as argument.
 */
@LayoutScopeMarker
@Immutable
interface DataTableScope {
    /**
     * Creates a new row in the [BasicDataTable] with the specified content.
     */
    fun row(
        onClick: (() -> Unit)? = null,
        key: Any? = null,
        content: TableRowScope.() -> Unit
    )

    /**
     * Creates new rows in the [BasicDataTable] with the specified content.
     */
    fun rows(
        count: Int,
        onClick: ((index: Int) -> Unit)? = null,
        key: ((index: Int) -> Any)? = null,
        content: TableRowScope.(Int) -> Unit
    )
}

@ExperimentalFoundationApi
internal class DataTableScopeImpl : DataTableScope {
    private val _tableRows = MutableIntervalList<TableRowData>()
    val tableRows: IntervalList<TableRowData> = _tableRows

    override fun row(
        onClick: (() -> Unit)?,
        key: Any?,
        content: TableRowScope.() -> Unit
    ) {
        _tableRows.addInterval(
            1,
            TableRowData(
                onClick = if (onClick != null) { _: Int -> onClick() } else null,
                key = if (key != null) { _: Int -> key } else null,
                item = { content() }
            )
        )
    }

    override fun rows(
        count: Int,
        onClick: ((index: Int) -> Unit)?,
        key: ((index: Int) -> Any)?,
        content: TableRowScope.(Int) -> Unit
    ) {
        _tableRows.addInterval(
            count,
            TableRowData(
                onClick = onClick,
                key = key,
                item = content
            )
        )
    }
}
