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

package com.seanproctor.datatable.material3

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.BasicDataTable
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.rememberDataTableState

/**
 * Layout model that arranges its children into rows and columns.
 */
@Composable
fun DataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    state: DataTableState = rememberDataTableState(),
    separator: @Composable () -> Unit = { HorizontalDivider() },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headerBackgroundColor: Color = Color.Unspecified,
    footerBackgroundColor: Color = Color.Unspecified,
    footer: @Composable () -> Unit = { },
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.() -> Unit
) {
    BasicDataTable(
        columns = columns,
        modifier = modifier,
        state = state,
        separator = separator,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        contentPadding = contentPadding,
        headerBackgroundColor = headerBackgroundColor,
        footerBackgroundColor = footerBackgroundColor,
        footer = footer,
        cellContentProvider = Material3CellContentProvider,
        sortColumnIndex = sortColumnIndex,
        sortAscending = sortAscending,
        logger = logger,
        content = content
    )
}
