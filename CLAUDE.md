# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

- `./gradlew assembleDebug` - Build debug APK
- `./gradlew assembleRelease` - Build release APK (signed with release keystore)
- `./gradlew clean` - Clean build artifacts
- `./gradlew build` - Build both debug and release variants
- `Release.bat` - Convenience script to build release APK

## Test Commands

- `./gradlew test` - Run unit tests
- `./gradlew connectedAndroidTest` - Run instrumented tests on connected device/emulator

## Project Architecture

This is an Android Jetpack Compose application for temporary email management with the following architecture:

### Core Stack
- **Kotlin** with Coroutines for async operations
- **Jetpack Compose** for UI (Material 3)
- **Koin** for dependency injection
- **OkHttp** for networking
- **Kotlinx Serialization** for JSON parsing
- **DataStore** for settings persistence

### Architecture Layers

1. **UI Layer** (`ui/`)
   - `MainActivity` - Entry point with theme management
   - `screens/` - Compose screens (MainScreen, EmailListScreen)
   - `viewmodel/` - ViewModels for state management
   - `components/` - Reusable UI components

2. **Data Layer** (`data/`)
   - `repository/` - Repository pattern implementations
     - `EmailRepository` - Manages email data and loading states
     - `TokenRepository` - Handles authentication tokens with auto-refresh
   - `network/` - Network services and API interfaces
     - `MailService` - Main API service for email operations
     - `MailCxApiService` - HTTP client wrapper
   - `model/` - Data models for Email, EmailDetails, AuthToken
   - `datastore/` - Settings persistence

3. **Dependency Injection** (`di/AppModule.kt`)
   - Koin module defining all dependencies
   - Singleton services and ViewModels
   - Network configuration

### Key Features
- Temporary email address generation and management
- Email list fetching with automatic refresh
- Email detail viewing
- Token-based authentication with automatic renewal
- Settings management with theme switching
- Comprehensive error handling and logging

### Important Implementation Details
- Token refresh is automatically handled in `EmailRepository` on 401 errors
- Network requests use Result pattern for error handling
- File logging is implemented for debugging network issues
- App uses proper lifecycle management for token cleanup