package com.seanproctor.datatable.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.seanproctor.datatable.DataTable
import com.seanproctor.datatable.DataColumn

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Row {
                DataTable(
                    columns = listOf(
                        DataColumn {
                            Text("Column1")
                        },
                        DataColumn {
                            Text("Column2")
                        },
                        DataColumn(Alignment.CenterEnd) {
                            Text("Column3")
                        },
                    ),
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                ) {
                    for (rowIndex in 0 until 40) {
                        row {
//                            onClick = { Log.d("datatable", "Row clicked: $rowIndex") }
                            cell {
                                Text((rowIndex * 3).toString())
                            }
                            cell {
                                Text((rowIndex * 3 + 1).toString(16))
                            }
                            cell {
                                Text((rowIndex * 3.0f + 2.0f).toString())
                            }
                        }
                    }
                }
                Text("after table")
            }
        }
    }
}
