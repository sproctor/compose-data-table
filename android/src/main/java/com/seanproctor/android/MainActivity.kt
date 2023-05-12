package com.seanproctor.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import com.seanproctor.datatable.Table
import com.seanproctor.datatable.TableColumnDefinition

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Row {
                    Table(
                        columns = listOf(
                            TableColumnDefinition {
                                Text("Column1")
                            },
                            TableColumnDefinition {
                                Text("Column2")
                            },
                            TableColumnDefinition(Alignment.CenterEnd) {
                                Text("Column3")
                            },
                        )
//            modifier = Modifier.fillMaxSize(),
                    ) {
                        row {
                            cell {
                                Text("One")
                            }
                            cell {
                                Text("Two")
                            }
                            cell {
                                Text("3.0")
                            }
                        }
                        row {
                            cell {
                                Text("Four")
                            }
                            cell {
                                Text("Five")
                            }
                            cell {
                                Text("6.0")
                            }
                        }
                    }
                    Text("after table")
                }
            }
        }
    }
}
