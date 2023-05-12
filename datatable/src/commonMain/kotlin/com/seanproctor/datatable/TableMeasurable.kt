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
 * Collects measurements for the children of a column that
 * are available to implementations of [TableColumnWidth].
 */
data class TableMeasurable internal constructor(
    /**
     * Computes the preferred width of the child by measuring with infinite constraints.
     */
    val preferredWidth: () -> Int,
    /**
     * Computes the minimum intrinsic width of the child for the given available height.
     */
    val minIntrinsicWidth: (Int) -> Int,
    /**
     * Computes the maximum intrinsic width of the child for the given available height.
     */
    val maxIntrinsicWidth: (Int) -> Int
)
