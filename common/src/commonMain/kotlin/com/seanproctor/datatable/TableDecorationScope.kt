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

/**
 * Collects the vertical/horizontal offsets of each row/column of a [Table] that are available
 * to a table decoration when its body is executed on a [TableDecorationScope] instance.
 */
interface TableDecorationScope {
    val verticalOffsets: List<Int>
    val horizontalOffsets: List<Int>
}

internal data class TableDecorationScopeImpl(
    override val verticalOffsets: List<Int>,
    override val horizontalOffsets: List<Int>
) : TableDecorationScope
