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

import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Used to specify the size of a [DataTable]'s column.
 */
abstract class TableColumnWidth private constructor(internal val flexValue: Float) {

    /**
     * Returns the ideal width of the column.
     *
     * Note that the column might be wider than this if it is flexible.
     */
    abstract fun preferredWidth(
        cells: List<TableMeasurable>,
        containerWidth: Int,
        density: Density
    ): Int

    /**
     * Returns the minimum intrinsic width of the column for the given height.
     *
     * This is used for computing the table's intrinsic dimensions. Defaults to [preferredWidth].
     */
    open fun minIntrinsicWidth(
        cells: List<TableMeasurable>,
        containerWidth: Int,
        density: Density,
        availableHeight: Int
    ): Int {
        return cells.maxOfOrNull { it.minIntrinsicWidth(availableHeight) } ?: 0
    }

    /**
     * Returns the minimum intrinsic width of the column for the given height.
     *
     * This is used for computing the table's intrinsic dimensions. Defaults to [preferredWidth].
     */
    open fun maxIntrinsicWidth(
        cells: List<TableMeasurable>,
        containerWidth: Int,
        density: Density,
        availableHeight: Int
    ): Int {
        return cells.minOfOrNull { it.maxIntrinsicWidth(availableHeight) } ?: 0
    }

    /**
     * An inflexible column has a fixed size which is computed by [preferredWidth].
     */
    abstract class Inflexible : TableColumnWidth(flexValue = 0f) {
        /**
         * Creates a column width specification which defaults to the width specified by the
         * [preferredWidth] of the receiver, but may also grow by taking a part of the remaining
         * space according to the given [flex] once all the inflexible columns have been measured.
         */
        fun flexible(flex: Float): TableColumnWidth = Flexible(flex, this)
    }

    private data class Flexible(val flex: Float, val other: Inflexible) : TableColumnWidth(flex) {

        override fun preferredWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density
        ): Int {
            return other.preferredWidth(cells, containerWidth, density)
        }

        override fun minIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return other.minIntrinsicWidth(cells, containerWidth, density, availableHeight)
        }

        override fun maxIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return other.maxIntrinsicWidth(cells, containerWidth, density, availableHeight)
        }
    }

    /**
     * Sizes the column by taking a part of the remaining space according to [flex] once all the
     * inflexible columns have been measured. Note that this defaults to 0 if no space is available.
     */
    data class Flex(private val flex: Float) : TableColumnWidth(flex) {
        override fun preferredWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density
        ): Int {
            return minIntrinsicWidth(cells, containerWidth, density, Constraints.Infinity)
        }
    }

    /**
     * Sizes the column to the width of the widest child in that column.
     *
     * Note that, in order to compute their preferred widths, the children will be measured with
     * infinite width constraints to prevent them from filling the available space. For a
     * wrap content behaviour without infinite measurements, use [minIntrinsicWidth] or
     * [maxIntrinsicWidth].
     */
    object Wrap : Inflexible() {
        override fun preferredWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density
        ): Int {
            return cells.fold(0) { acc, cell ->
                max(acc, cell.preferredWidth())
            }
        }

        override fun minIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return cells.fold(0) { acc, cell ->
                max(acc, cell.minIntrinsicWidth(availableHeight))
            }
        }

        override fun maxIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return cells.fold(0) { acc, cell ->
                max(acc, cell.maxIntrinsicWidth(availableHeight))
            }
        }
    }

    /**
     * Sizes the column to the largest of the minimum intrinsic widths of the children in that
     * column (i.e. the minimum width such that children can layout/paint themselves correctly).
     *
     * Note that this is a very expensive way to size a column. For a wrap content behaviour that
     * skips the intrinsic measurements which compute the column width before measuring, use [Wrap].
     */
    object MinIntrinsic : Inflexible() {
        override fun preferredWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density
        ): Int {
            return cells.fold(0) { acc, cell ->
                max(acc, cell.minIntrinsicWidth(Constraints.Infinity))
            }
        }

        override fun minIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return cells.fold(0) { acc, cell ->
                max(acc, cell.minIntrinsicWidth(availableHeight))
            }
        }

        override fun maxIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return cells.fold(0) { acc, cell ->
                max(acc, cell.maxIntrinsicWidth(availableHeight))
            }
        }
    }

    /**
     * Sizes the column to the largest of the maximum intrinsic widths of the children in that
     * column (i.e. the maximum width such that children can occupy the entire space without waste).
     *
     * Note that this is a very expensive way to size a column. For a wrap content behaviour that
     * skips the intrinsic measurements which compute the column width before measuring, use [Wrap].
     */
    object MaxIntrinsic : Inflexible() {
        override fun preferredWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density
        ): Int {
            return cells.fold(0) { acc, cell ->
                max(acc, cell.maxIntrinsicWidth(Constraints.Infinity))
            }
        }

        override fun minIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return cells.fold(0) { acc, cell ->
                max(acc, cell.minIntrinsicWidth(availableHeight))
            }
        }

        override fun maxIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return cells.fold(0) { acc, cell ->
                max(acc, cell.maxIntrinsicWidth(availableHeight))
            }
        }
    }

    /**
     * Sizes the column to a specific width.
     */
    data class Fixed(private val width: Dp) : Inflexible() {
        override fun preferredWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density
        ): Int {
            return with(density) { width.roundToPx() }
        }
    }

    /**
     * Sizes the column to a fraction of the table's maximum width constraint.
     *
     * Note that this defaults to 0 if the maximum width constraints is infinite.
     */
    data class Fraction(
        private val fraction: Float
    ) : Inflexible() {
        override fun preferredWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density
        ): Int {
            return if (containerWidth != Constraints.Infinity) {
                (containerWidth * fraction).roundToInt()
            } else {
                0
            }
        }
    }

    /**
     * Sizes the column to the size that is the minimum of two column width specifications.
     *
     * Both specifications are evaluated, so if either specification is expensive, so is this.
     */
    data class Min(private val a: Inflexible, private val b: Inflexible) : Inflexible() {
        override fun preferredWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density
        ): Int {
            return min(
                a.preferredWidth(cells, containerWidth, density),
                b.preferredWidth(cells, containerWidth, density)
            )
        }

        override fun minIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return min(
                a.minIntrinsicWidth(cells, containerWidth, density, availableHeight),
                b.minIntrinsicWidth(cells, containerWidth, density, availableHeight)
            )
        }

        override fun maxIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return min(
                a.maxIntrinsicWidth(cells, containerWidth, density, availableHeight),
                b.maxIntrinsicWidth(cells, containerWidth, density, availableHeight)
            )
        }
    }

    /**
     * Sizes the column to the size that is the maximum of two column width specifications.
     *
     * Both specifications are evaluated, so if either specification is expensive, so is this.
     */
    data class Max(private val a: Inflexible, private val b: Inflexible) : Inflexible() {
        override fun preferredWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density
        ): Int {
            return max(
                a.preferredWidth(cells, containerWidth, density),
                b.preferredWidth(cells, containerWidth, density)
            )
        }

        override fun minIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return max(
                a.minIntrinsicWidth(cells, containerWidth, density, availableHeight),
                b.minIntrinsicWidth(cells, containerWidth, density, availableHeight)
            )
        }

        override fun maxIntrinsicWidth(
            cells: List<TableMeasurable>,
            containerWidth: Int,
            density: Density,
            availableHeight: Int
        ): Int {
            return max(
                a.maxIntrinsicWidth(cells, containerWidth, density, availableHeight),
                b.maxIntrinsicWidth(cells, containerWidth, density, availableHeight)
            )
        }
    }
}
