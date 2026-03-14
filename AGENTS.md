# Barnee — Agent Guide

Cocktail recipe app built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**,
targeting Android and iOS.

## Project Structure

```
app/        Android application entry point (MainActivity, App)
shared/     KMP business logic (data, domain, DI) — shared between Android & iOS
ui/         KMP Compose Multiplatform UI (screens, components, theme)
iosApp/     iOS host app (SwiftUI + CocoaPods)
```

Source sets inside `shared/` and `ui/`:

- `commonMain` — shared Kotlin code
- `androidMain` — Android-specific implementations
- `iosMain` — iOS-specific implementations

## Architecture

**MVI** with a reactive `StateMachine` base class (in
`shared/src/commonMain/.../domain/StateMachine.kt`):

- Each feature has a `*StateMachine` that implements `State`, `Action`, and a `Reducer`
- `StateMachine` extends Voyager's `ScreenModel` and is managed by Koin DI
- Navigation is handled via `Router` and Voyager screens defined in `ui/`

**Layers:**

- `data/model` — pure data classes (serializable, `@Parcelize`)
- `data/remote` — Ktor-based API clients (`Api`, `AiApi`, `CloudinaryApi`)
- `data/repository` — repositories bridging remote + local
- `data/local` — `LocalStore` backed by Multiplatform Settings / DataStore
- `domain/` — state machines and use cases
- `ui/screen/` — Compose Multiplatform screens, one per feature

## Key Dependencies

| Library                | Purpose                                          |
|------------------------|--------------------------------------------------|
| Voyager                | Navigation + `ScreenModel` (ViewModel)           |
| Koin                   | Dependency injection (multiplatform)             |
| Ktor                   | HTTP client (cocktail API, OpenAI, Cloudinary)   |
| Kotlinx Serialization  | JSON parsing                                     |
| Multiplatform Settings | Local persistence (DataStore on Android)         |
| OpenAI client          | AI cocktail generation (`BartenderStateMachine`) |
| Compose Multiplatform  | Shared UI for Android and iOS                    |

## Build Setup

**Prerequisites:**

- JDK 17+ on `PATH` (install via `brew install --cask zulu@17`)
- Android SDK at `sdk.dir` set in `local.properties`
- API keys in `local.properties`:
  ```
  OPEN_AI_API_KEY=...
  CLOUDINARY_API_SECRET=...
  ```
- For iOS: CocoaPods installed; run `pod install` inside `iosApp/`

**Build commands:**

```bash
# Android debug APK
./gradlew :app:assembleDebug

# Run Android unit tests
./gradlew :shared:testDebugUnitTest
./gradlew :ui:testDebugUnitTest

# Check for dependency updates
./gradlew dependencyUpdates

# iOS — generate the shared framework first
./gradlew :shared:podPublishDebugXCFramework
```

## Coding Conventions

- **New feature checklist:**
    1. Add `State`, `Action`, `Reducer` → create `*StateMachine` in `shared/.../domain/`
    2. Register the state machine as a `factory` in `commonModule` (`shared/.../di/Koin.kt`)
    3. Add a Voyager `Screen` in `ui/.../screen/`
    4. Register the screen in `ui/.../navigation/ScreenRegistry.kt`
    5. Add a route in `shared/.../domain/navigation/AppScreens.kt` if deep-linkable

- **Platform-specific code:** use `expect`/`actual` with implementations in `androidMain` /
  `iosMain`
- **All copyright headers** must use the MIT license block matching the rest of the project
- **Package name:** `com.popalay.barnee`
- **Kotlin style:** official (`kotlin.code.style=official` in `gradle.properties`)
- Do not add inline imports; keep all imports at the top of the file

## Screens

| Screen                   | StateMachine                         |
|--------------------------|--------------------------------------|
| Discovery                | `DiscoveryStateMachine`              |
| Drink detail             | `DrinkStateMachine`                  |
| Search                   | `SearchStateMachine`                 |
| Parameterized drink list | `ParameterizedDrinkListStateMachine` |
| Collection               | `CollectionStateMachine`             |
| Collection list          | `CollectionListStateMachine`         |
| Add to collection        | `AddToCollectionStateMachine`        |
| Bartender (AI)           | `BartenderStateMachine`              |
| Shake to drink           | `ShakeToDrinkStateMachine`           |
