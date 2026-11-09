package com.seanproctor.datatable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp

interface CellContentProvider {
    @Composable
    fun RowCellContent(content: @Composable () -> Unit)

    @Composable
    fun HeaderCellContent(
        sorted: Boolean,
        sortAscending: Boolean,
        isSortIconTrailing: Boolean,
        onClick: (() -> Unit)?,
        content: @Composable () -> Unit
    )

    /**
     * Renders the selection checkbox at the start of a body row in a selectable table.
     *
     * @param selected Whether the row is selected.
     * @param onSelectedChange Called with the new selection value when toggled, or null if the
     * row can't be toggled.
     */
    @Composable
    fun SelectionCellContent(
        selected: Boolean,
        onSelectedChange: ((Boolean) -> Unit)?,
    ) {
        BasicSelectionCheckbox(
            state = ToggleableState(selected),
            onClick = onSelectedChange?.let { { it(!selected) } },
        )
    }

    /**
     * Renders the "select all" checkbox at the start of the header row in a selectable table.
     *
     * @param state Whether all, some, or none of the rows are selected.
     * @param onClick Called when the checkbox is clicked, or null if it's disabled.
     */
    @Composable
    fun HeaderSelectionCellContent(
        state: ToggleableState,
        onClick: (() -> Unit)?,
    ) {
        BasicSelectionCheckbox(state = state, onClick = onClick)
    }
}

object DefaultCellContentProvider : CellContentProvider {

    @Composable
    override fun RowCellContent(content: @Composable () -> Unit) {
        content()
    }

    @Composable
    override fun HeaderCellContent(
        sorted: Boolean,
        sortAscending: Boolean,
        isSortIconTrailing: Boolean,
        onClick: (() -> Unit)?,
        content: @Composable () -> Unit
    ) {
        content()
    }
}

/**
 * A minimal, foundation-only tri-state checkbox used by the default selection cells. Styling
 * libraries (such as `datatable-material3`) override the selection cells with themed checkboxes.
 */
@Composable
private fun BasicSelectionCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
) {
    val color = if (onClick != null) Color.DarkGray else Color.LightGray
    Box(
        Modifier
            .then(
                if (onClick != null) {
                    Modifier.triStateToggleable(state = state, role = Role.Checkbox, onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(4.dp)
            .size(18.dp)
            .border(2.dp, color, RoundedCornerShape(2.dp))
            .padding(4.dp)
    ) {
        when (state) {
            ToggleableState.On -> Box(Modifier.fillMaxSize().background(color))
            ToggleableState.Indeterminate -> Box(
                Modifier.fillMaxWidth().height(2.dp).align(Alignment.Center).background(color)
            )
            ToggleableState.Off -> Unit
        }
    }
}
