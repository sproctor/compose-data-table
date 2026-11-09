# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

A Compose Multiplatform library implementing the [Material Design data table](https://m2.material.io/components/data-tables) spec. Originally derived from the table implementation removed from Compose pre-1.0. Published to Maven Central as `com.seanproctor:datatable` and `com.seanproctor:datatable-material3`. Library targets: Android, JVM (desktop), iOS (arm64/simulatorArm64), JS, and Wasm/JS.

Requires JDK 17+ to build (enforced in `settings.gradle.kts`). Built with Android Gradle Plugin 9 (built-in Kotlin) and Gradle 9.

## Modules

Library:
- **`:datatable`** — Core, unstyled implementation. Depends only on `compose.foundation`. This is where the layout, measurement, sorting, scrolling, and pagination logic lives.
- **`:datatable-material3`** — Thin Material 3 styling layer. `api`-depends on `:datatable` and provides `DataTable`/`PaginatedDataTable` wrappers that supply a `Material3CellContentProvider` plus M3 defaults (dividers, 56dp header / 52dp rows, typography).

Demo (under `demo/`, all consume the library):
- **`:demo:shared`** — KMP library (`com.android.kotlin.multiplatform.library`) holding the shared `App()` composable (`commonMain`) and the iOS `MainViewController` (`iosMain`, framework `baseName = "DemoApp"`). Targets android/jvm/wasmJs/iosArm64/iosSimulatorArm64.
- **`:demo:androidApp`** — `com.android.application` (MainActivity + manifest), depends on `:demo:shared`.
- **`:demo:desktopApp`** — `kotlin("jvm")` + Compose Desktop (`Main.kt`), depends on `:demo:shared`.
- **`:demo:webApp`** — KMP wasmJs-only browser app (`main.kt` + `index.html`), depends on `:demo:shared`.
- **`demo/iosApp`** — Xcode project (not a Gradle module). Links the `DemoApp` framework via a build phase that runs `:demo:shared:embedAndSignAppleFrameworkForXcode`; framework search path is `$(SRCROOT)/../shared/build/...`.

The demo is split into per-platform app modules because AGP 9 no longer allows `com.android.application` to coexist with `org.jetbrains.kotlin.multiplatform` in one module — the Android app must depend on a separate KMP library module (`:demo:shared`).

Benchmarks:
- **`:benchmark`** — JVM-only JMH suite (kotlinx-benchmark) that renders tables into a headless `ImageComposeScene` and times full frames (compose + layout + draw). Not published. See `benchmark/README.md` for what each benchmark measures.

- **`build-logic`** — Included build with convention plugins (see below). Not a regular subproject.

## Architecture

The core abstraction layers, from low to high:

1. **`BasicDataTable` / `BasicPaginatedDataTable`** (in `:datatable`) — The unstyled engine. `BasicDataTable` uses a `SubcomposeLayout` to measure rows/columns and render header, body, and footer slots. All sizing, alignment, scrolling, and sort-icon plumbing happens here.
2. **`CellContentProvider`** — The styling seam between core and Material 3. `BasicDataTable` delegates rendering of each cell (`RowCellContent`) and header (`HeaderCellContent`, including the sort UI) to an injected provider. `DefaultCellContentProvider` renders content as-is; `Material3CellContentProvider` wraps it with M3 typography and sort icon buttons. To change how cells/headers look without touching layout, implement a `CellContentProvider`.
3. **`DataTable` / `PaginatedDataTable`** (in `:datatable-material3`) — Public convenience wrappers that inject the M3 provider and defaults.

Key pieces:
- **DSL** (`DataTableDsl.kt`) — `DataTableScope.row { ... }` builds `TableRowScopeImpl`, and `TableRowScope.cell { ... }` collects cell composables. Rows carry `onClick`, `height`, `isHeader`, `isFooter`, `backgroundColor`, `selected`, `onSelectedChange`. The content lambda runs eagerly to collect rows before layout.
- **Columns** (`DataColumn.kt`) — Each column has an `alignment`, a `TableColumnWidth`, an optional `onSort` callback, and a header composable.
- **Column widths** (`TableColumnWidth.kt`) — Sealed hierarchy: `Flex`, `Fixed`, `Fraction`, `Wrap`, `MinIntrinsic`, `MaxIntrinsic`, plus combinators `Min`/`Max` and `.flexible(flex)`. Drives the measurement pass.
- **State** — `DataTableState` (+ `rememberDataTableState`) holds scroll state. `PaginatedDataTableState` (+ `rememberPaginatedDataTableState`) adds `count`, `pageIndex`, and `PageSize` (`FixedSize` or `FillMaxHeight`); the paginated content lambda receives `(fromIndex, toIndex)`.
- **Selection** — `selectable = true` makes `BasicDataTable` prepend an internal checkbox column (`effectiveColumns`); caller-facing column indexes (`sortColumnIndex`, `onSort`, `TableCellScope.columnIndex`) stay relative to the caller's `columns`. State is hoisted per row (`selected`/`onSelectedChange`); the header's `ToggleableState` is derived from body rows unless `selectAllState` is passed. Checkboxes render via `CellContentProvider.SelectionCellContent`/`HeaderSelectionCellContent` (default bodies in the interface, M3 overrides).
- **Sorting** — Driven by `sortColumnIndex`/`sortAscending` params plus per-column `onSort`. The provider renders the sort icon; `isSortIconTrailing` controls icon placement.

## Build conventions

Library modules apply two convention plugins from `build-logic` rather than configuring KMP/publishing inline:
- **`datatable.library`** (`LibraryConventionPlugin`) — Applies the KMP, Android library, and Compose plugins and configures all targets via `configureKotlinMultiplatform` (`KotlinAndroid.kt`): `jvmToolchain(11)`, Android `minSdk=21`/`compileSdk=36`, namespace derived from module name, plus jvm/js/wasmJs/iOS (arm64 + simulatorArm64) targets.
- **`datatable.publish`** (`PublishConventionPlugin`) — Vanniktech Maven publish to Maven Central, signing, and POM metadata.

When adding a new published library module, apply both plugins (see `datatable/build.gradle.kts`) and add it to `settings.gradle.kts`. The demo modules do **not** use these plugins (they configure KMP/Android inline and are not published).

AGP 9 note: the `com.android.kotlin.multiplatform.library` Android target is configured via the `androidLibrary { }` DSL in `.kts` build files (`:demo:shared`). The precompiled convention plugin can't use that generated accessor, so `KotlinAndroid.kt` configures it through the named extension instead: `extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("androidLibrary") { ... }`. The Android app module uses AGP 9's **built-in Kotlin** (no `kotlin.android` plugin — applying it is an error under AGP 9).

Dependencies are managed in `gradle/libs.versions.toml` (version catalog). All plugin versions are declared once in the root `build.gradle.kts` `plugins { ... apply false }` block; modules then apply them via `alias(...)` without a version. The project version is set by `datatable.version` in `gradle.properties`.

## Common commands

```bash
# Build everything
./gradlew build

# Run the demo on desktop (JVM)
./gradlew :demo:desktopApp:run

# Run the demo in the browser (Wasm/JS)
./gradlew :demo:webApp:wasmJsBrowserDevelopmentRun

# Build the Android demo APK / install on a device
./gradlew :demo:androidApp:assembleDebug
./gradlew :demo:androidApp:installDebug

# Desktop hot reload
./gradlew :demo:desktopApp:hotRun

# Run all checks / tests across targets
./gradlew check
./gradlew allTests

# Publish a local test repo (to ./build/testMaven of each module)
./gradlew publishAllPublicationsToTestMavenRepository

# Benchmarks (full run ~7 min; smoke run is quick; filter is a regex on class/method name)
./gradlew :benchmark:benchmark
./gradlew :benchmark:smokeBenchmark -PbenchmarkFilter=Scroll
```

There is currently no unit/instrumented test source set in the library modules.

## Publishing / CI

Releases are automated: creating a GitHub Release triggers `.github/workflows/build.yml`, which runs `./gradlew build` then `./gradlew publishAndReleaseToMavenCentral` on `macos-latest` (needed for iOS targets). Maven Central and GPG signing credentials come from repository secrets / `~/.gradle/gradle.properties`.

## Conventions

- Kotlin official code style. Import ordering and star-import thresholds are pinned in `.editorconfig` (`*,java.**,javax.**,kotlin.**,^`, no star imports). License/copyright templates live in `spotless/`.
- Files derived from AOSP retain the Apache 2.0 header; new files in those modules should keep it consistent.
