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
import androidx.compose.runtime.Composable

@LayoutScopeMarker
interface TableRowScope {
    val rowIndex: Int

    fun cell(content: @Composable TableCellScope.() -> Unit)
}

internal data class TableRowScopeImpl(override val rowIndex: Int) : TableRowScope {
    val cells = mutableListOf<TableCellData>()

    override fun cell(content: @Composable TableCellScope.() -> Unit) {
        cells += TableCellData(content)
    }
}
