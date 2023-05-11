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

import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density

/**
 * Parent data associated with children to assign a row group.
 */
internal data class TableParentData(
    val rowIndex: Int
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@TableParentData
}

internal val IntrinsicMeasurable.rowIndex get() = (parentData as? TableParentData)?.rowIndex
