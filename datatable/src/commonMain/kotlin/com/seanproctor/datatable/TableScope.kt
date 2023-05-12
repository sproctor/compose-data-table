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

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Immutable

/**
 * Collects information about the children of a [Table] when
 * its body is executed with a [TableScope] as argument.
 */
@LayoutScopeMarker
@Immutable
interface TableScope {
    /**
     * Creates a new row in the [Table] with the specified content.
     */
    fun row(content: TableRowScope.() -> Unit)
}

internal class TableScopeImpl : TableScope {
    val tableRows = mutableListOf<TableRowScope.() -> Unit>()

    override fun row(content: TableRowScope.() -> Unit) {
        tableRows += content
    }
}
