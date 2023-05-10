package com.seanproctor.datatable

import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density

/**
 * Parent data associated with children to assign a row group.
 */
internal data class TableParentData(
    val rowIndex: Int,
    val columnIndex: Int
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@TableParentData
}

internal val IntrinsicMeasurable.rowIndex get() = (parentData as? TableParentData)?.rowIndex
internal val IntrinsicMeasurable.columnIndex get() = (parentData as? TableParentData)?.columnIndex
