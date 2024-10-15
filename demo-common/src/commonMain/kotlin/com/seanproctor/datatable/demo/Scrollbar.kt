package com.seanproctor.datatable.demo

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seanproctor.datatable.DataTableScrollState

@Composable
expect fun VerticalScrollbar(scrollState: DataTableScrollState, modifier: Modifier)

@Composable
expect fun HorizontalScrollbar(scrollState: DataTableScrollState, modifier: Modifier)