# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TempMail is an Android application that provides temporary/disposable email services. Users can generate temporary email addresses to avoid spam and protect their privacy when registering for services. The app is built entirely in Kotlin using modern Android architecture patterns.

## Build System

This project uses Gradle with Kotlin DSL (.kts files).

### Development Commands

- **Build the project**: `./gradlew build`
- **Clean build**: `./gradlew clean`
- **Build debug APK**: `./gradlew assembleDebug`
- **Build release APK**: `./gradlew assembleRelease`
- **Run tests**: `./gradlew test`
- **Run instrumented tests**: `./gradlew connectedAndroidTest`
- **Install debug build**: `./gradlew installDebug`

### Release Build

The project has signing configuration in `app/signing.properties`. Use `Release.bat` for automated release builds.

## Architecture

### Technology Stack

- **UI Framework**: Jetpack Compose with Material3
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Koin 4.1.0
- **Networking**: OkHttp 5.1.0 with custom API service
- **Data Storage**: DataStore Preferences
- **Serialization**: Kotlinx Serialization
- **Build Target**: Android API 36, minimum API 26

### Project Structure

```
app/src/main/kotlin/com/temp/mail/
├── data/
│   ├── datastore/          # Settings persistence
│   ├── model/              # Data models (Email, AuthToken, etc.)
│   ├── network/            # API services and networking
│   └── repository/         # Repository implementations
├── di/                     # Dependency injection modules
├── ui/
│   ├── components/         # Reusable UI components
│   ├── screens/            # Compose screens
│   ├── settings/           # Settings UI and ViewModels
│   ├── theme/              # Material3 theming
│   └── viewmodel/          # ViewModels for screens
└── util/                   # Utilities (logging, email generation, etc.)
```

### Key Architecture Components

1. **Repository Pattern**: `EmailRepository` and `TokenRepository` handle data operations
2. **Token Management**: Automatic token refresh with `TokenRepository.startTokenRefresh()`
3. **StateFlow**: Reactive state management throughout the app
4. **Koin DI**: All dependencies configured in `di/AppModule.kt`
5. **MailService**: Abstracted API layer with automatic token refresh on 401 errors

### Data Flow

1. `App.kt` initializes Koin and starts token management
2. ViewModels observe repositories via StateFlow
3. Repositories handle API calls and automatic token refresh
4. UI components observe ViewModels for reactive updates

## API Integration

The app integrates with mail.cx API service:
- Base URL configured in `strings.xml`
- Automatic token refresh on authentication failures
- Email list and detail retrieval
- Token management with cleanup on app termination

## Important Files

- `App.kt`: Application class with DI initialization
- `di/AppModule.kt`: Koin dependency injection configuration
- `data/repository/EmailRepository.kt`: Core email data operations
- `data/repository/TokenRepository.kt`: Authentication token management
- `data/network/MailService.kt`: API service abstraction