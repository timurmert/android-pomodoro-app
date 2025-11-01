# Repository Guidelines

## Project Structure & Module Organization
The Android project follows the default Gradle layout. Application code lives in `app/src/main/java`, grouped by feature (`timer/`, `sessions/`, `settings/`). Shared UI components go under `app/src/main/java/.../ui/components`. XML resources and Compose themes sit in `app/src/main/res`. Unit tests belong in `app/src/test`, while instrumented tests and Espresso scenarios live in `app/src/androidTest`. Keep assets such as icons or sounds in `app/src/main/res/drawable` or `raw`. Configuration files (`build.gradle.kts`, `gradle.properties`) define module dependencies—update them when introducing new libraries.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` — compile the debug APK and fail fast on compile errors.
- `./gradlew lint` — run Android Lint with project defaults to catch styling and resource issues.
- `./gradlew testDebugUnitTest` — execute JVM unit tests.
- `./gradlew connectedDebugAndroidTest` — run instrumented tests on an attached emulator or device.
- `./gradlew ktlintCheck` — verify Kotlin formatting before raising a PR.

## Coding Style & Naming Conventions
Use Kotlin with 4-space indentation and meaningful names. Classes, interfaces, and composables are `PascalCase`; functions, properties, and parameters use `camelCase`. ViewModel factories end with `Factory`, and Compose previews append `Preview`. Resource IDs follow `snake_case`. Prefer immutable data classes and top-level constants in `UPPER_SNAKE_CASE`. Run `ktlintFormat` when touching Kotlin files and keep Gradle scripts in Kotlin DSL style.

## Testing Guidelines
Cover new logic with unit tests under `src/test`, mocking long-running work. Place instrumentation paths and UI flows in `src/androidTest` and tag them with the feature (`TimerFlowTest`). Aim to leave coverage equal or higher than before; update flaky tests instead of ignoring them. Use `@RunWith(AndroidJUnit4::class)` and descriptive test method names such as `shouldStartPomodoroWhenDurationSelected`.

## Commit & Pull Request Guidelines
Write concise, imperative commit subjects (e.g., `Add long-break notification`). Group related changes per commit and include relevant Gradle or resource updates. Pull requests should explain intent, list manual test results, link issues (e.g., `Closes #123`), and attach screenshots or recordings when UI changes. Ensure CI passes and request review from another contributor before merging.

## Environment & Configuration
Store secrets (API keys, service URLs) outside of VCS in `local.properties` or environment variables. Document any new configuration in the PR description and README. When adding third-party services, update `LICENSE` notices if required and regenerate release notes.
