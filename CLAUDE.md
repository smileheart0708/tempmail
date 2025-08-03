# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android email application built with Kotlin and Jetpack Compose. The project uses modern Android development practices including dependency injection with Koin, DataStore for preferences, and Material 3 design.

## Build Commands

### Development
- **Debug build**: `./gradlew assembleDebug`
- **Release build**: `./gradlew assembleRelease` (or use `Release.bat` on Windows)
- **Install debug**: `./gradlew installDebug`
- **Clean build**: `./gradlew clean`

### Testing
- **Unit tests**: `./gradlew test`
- **Instrumented tests**: `./gradlew connectedAndroidTest`
- **Run single test class**: `./gradlew test --tests "com.temp.mail.ExampleUnitTest"`
- **Run specific test method**: `./gradlew test --tests "*.testMethod"`

### Code Quality
- **Lint**: `./gradlew lint`
- **Lint with auto-fix**: `./gradlew lintFix`

## Architecture

### Dependency Injection
- Uses **Koin** for dependency injection
- Configuration in `di/AppModule.kt`
- Modules defined for DataStore and ViewModels

### Data Layer
- **DataStore**: Used for app preferences (theme settings)
- Location: `data/datastore/SettingsDataStore.kt`
- Stores theme preference with options: "Light", "Dark", "System"
- **Models**: Email-related data models in `data/model/`
  - `EmailAddress.kt`: Core email address entity with id, address, active status, and creation timestamp
  - `Email.kt`: Email list item with id, subject, from, date, read status
  - `EmailDetail.kt`: Full email content with body (text/html), includes `EmailBody` data class

### UI Layer
- **Jetpack Compose** with Material 3
- **MVVM pattern** with ViewModels
- Main components:
  - `MainActivity`: Entry point with theme configuration
  - `MainScreen`: Primary UI with navigation drawer
  - `AppDrawer`: Navigation drawer with placeholder menu items
  - `SettingsActivity`: Settings screen (referenced but not fully implemented)

### Key Components
- **App.kt**: Application class that initializes Koin
- **MainViewModel**: Currently minimal, placeholder for main business logic
- **SettingsViewModel**: Manages theme preferences using DataStore

### Package Structure
```
com.temp.mail/
├── App.kt (Application class)
├── MainActivity.kt (Main activity)
├── data/datastore/ (Data persistence)
├── di/ (Dependency injection)
├── ui/
│   ├── components/ (Reusable UI components)
│   ├── screens/ (Screen composables)
│   ├── settings/ (Settings-related UI)
│   ├── theme/ (Theme configuration)
│   └── viewmodel/ (ViewModels)
```

## Development Configuration

### Build Configuration
- **Compile SDK**: 36
- **Min SDK**: 25
- **Target SDK**: 36
- **Java Version**: 17
- **Kotlin**: 2.2.0
- **AGP**: 8.11.0
- **Version catalogs**: Dependencies managed via `gradle/libs.versions.toml`

### Key Dependencies
- Jetpack Compose BOM: 2025.07.00
- Koin: 4.1.0
- OkHttp: 5.1.0
- DataStore: 1.1.7

### Signing
- Release builds use signing configuration from `signing.properties`
- KeyStore file: `smileheart.jks`
- ProGuard optimization enabled for release builds

## Development Workflow

### File Organization
- **Source code**: `app/src/main/kotlin/com/temp/mail/`
- **Resources**: `app/src/main/res/`
- **Tests**: `app/src/test/` (unit) and `app/src/androidTest/` (instrumented)
- **Localization**: Chinese translations available in `values-zh-rCN/`

### When adding new features:
1. Follow the MVVM pattern with ViewModels for business logic
2. Use Jetpack Compose for UI components
3. Register new ViewModels in `di/AppModule.kt` 
4. Add new data models to `data/model/` package
5. Use DataStore for persistent app settings

## Notes for Development

- The app currently shows placeholder content (list of numbered items)
- Navigation drawer has placeholder menu items that need implementation
- Settings screen exists but needs full implementation
- Network dependencies (OkHttp) are included but not yet utilized
- The project follows Material 3 design guidelines
- Theme switching is implemented and functional