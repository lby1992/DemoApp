A modern Android sample app built with **Kotlin** and **Jetpack Compose**.

This project demonstrates a clean architecture approach using ***MVVM***, ***Coroutines***, and
***Flow*** for state management.

## Design System

This project uses *Material Design 3* as the underlying design system, and all components follow the
M3 guidelines.

## Data Layer

The **data layer** is responsible for managing application data and serving it to upper layers in a
consistent and reliable way. It abstracts the underlying data sources and provides a clean API for
the rest of the app.

### Local Data Sources

#### DataStore

This project uses `DataStore` to persist application preferences locally. Preferences are stored
using the [Protocol Buffers (proto)](https://protobuf.dev/) format, which provides:

- **Type Safety** through a strongly-typed schema.
- **Better performance** compared to key-value storage(legacy *SharedPreferences*).
- **Smaller storage size**.
- **Forware/backward compatibility** for future schema changes.

The configuration of *proto* is located in the gradle module named `"data-proto"`.