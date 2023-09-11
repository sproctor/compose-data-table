package com.seanproctor.datatable.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.seanproctor.datatable.demo.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(
                onRowClick = { Log.d("Demo", "Row clicked: $it") }
            )
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App {}
}
