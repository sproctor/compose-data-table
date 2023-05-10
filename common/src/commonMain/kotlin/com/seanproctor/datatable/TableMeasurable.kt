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
