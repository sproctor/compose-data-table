![Maven Central](https://img.shields.io/maven-central/v/com.seanproctor/datatable)

# Compose Data Table

This library is an implementation of the [Material Design data table](https://m2.material.io/components/data-tables) for Compose.

The original code is derived from the implementation that was removed from Compose pre-1.0. The goal is to implement the entire Material Design spec for data tables.

![screenshot](screenshot.png)

## Getting started

Add the dependency to your gradle build file:

Non-material version:

```kotlin
implementation("com.seanproctor:datatable:<VERSION>")
```

Material 3:

```kotlin
implementation("com.seanproctor:datatable-material3:<VERSION>")
```

Draw a table

```kotlin
var selectedRow by remember { mutableStateOf<Int?>(null) }
DataTable(
    columns = listOf(
        DataColumn {
            Text("Header A")
        },
        DataColumn {
            Text("Header B")
        },
        DataColumn(Alignment.CenterEnd) {
            Text("Header C")
        },
    )
) {
    row {
        onClick = { selectedRow = 0 }
        cell { Text("Cell A1") }
        cell { Text("Cell B1") }
        cell { Text("Cell C1") }
    }
    row {
        onClick = { selectedRow = 1 }
        cell { Text("Cell A2") }
        cell { Text("Cell B2") }
        cell { Text("Cell C2") }
    }
}
```


Draw paginated table

```kotlin
PaginatedDataTable(
    columns = listOf(
        DataColumn {
            Text("Column1")
        },
        DataColumn {
            Text("Column2")
        },
        DataColumn {
            Text("Column3")
        },
    ),
    state = rememberPaginatedDataTableState(
        count = 100,
        pageSize = PageSize.FillMaxHeight
    ),
) { fromIndex, toIndex ->
    for (rowIndex in fromIndex until toIndex) {
        row {
            onClick = { println("Row clicked: $rowIndex") }
            cell {
                Text("Row $rowIndex, column 1")
            }
            cell {
                Text("Row $rowIndex, column 2")
            }
            cell {
                Text("Row $rowIndex, column 3")
            }
        }
    }
}
```


Selectable rows

Set `selectable = true` to add a leading checkbox column. Selection state is owned by the caller: each
row reports whether it is `selected` and handles `onSelectedChange`. The header checkbox shows whether
all, some, or none of the rows are selected; pass `selectAllState` to override it (for example, when a
selection spans pages of a paginated table). Rows without an `onClick` toggle their selection when
clicked.

```kotlin
val items = listOf("Apple", "Banana", "Cherry")
var selection by remember { mutableStateOf(emptySet<String>()) }
DataTable(
    columns = listOf(DataColumn { Text("Fruit") }),
    selectable = true,
    onSelectAllChange = { selectAll -> selection = if (selectAll) items.toSet() else emptySet() },
) {
    for (item in items) {
        row {
            selected = item in selection
            onSelectedChange = { selection = if (it) selection + item else selection - item }
            cell { Text(item) }
        }
    }
}
```
