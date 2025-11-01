# Pomodoro Focus

Pomodoro Focus is a multi-module Kotlin/Compose Pomodoro timer optimized for Redmi/MIUI devices. The app keeps sessions resilient with a foreground service, exact alarms, WorkManager recovery, and persistent state in Room/DataStore while presenting Material 3 Compose screens.

## Project Layout
- `app/` – Android application module with Compose UI, navigation, Hilt wiring, foreground TimerService, receivers, widget, and quick settings tile.
- `data/` – Android library providing Room-backed session storage, DataStore settings, and DI bindings.
- `domain/` – Pure Kotlin module defining the core models, repository contracts, and use cases.
- `signing/` – Sample `signing-sample.properties` to copy and populate for release signing.

## Build & Run
1. Install the Android SDK (API 35), Android Build Tools, and JDK 17.
2. From the project root run:
   - `./gradlew assembleDebug` to build a debug APK.
   - `./gradlew connectedDebugAndroidTest` to execute UI/instrumented tests (emulator or device required).
   - `./gradlew test` to run JVM unit tests across modules.
3. Launch the app on a device/emulator running Android 7.0 (API 24) or later.

## Release Signing & Universal APK
1. Create or reuse a release keystore and copy `signing/signing-sample.properties` to `signing/signing.properties`, updating the keystore path and credentials.
2. Build a signed, minified release and universal APK:
   - `./gradlew assembleRelease` for the standard ABI-split APKs.
   - `./gradlew assembleReleaseUniversalApk` to package a signed universal APK at `app/build/outputs/apk/release/app-release-universal.apk`.
3. Bump `version.properties` with `./gradlew bumpVersionCode` before tagging a release.

## Sideload Installation
1. Enable developer options and USB debugging on the device.
2. Install the universal APK with `adb install -r app/build/outputs/apk/release/app-release-universal.apk`.
3. On first launch, grant notification permissions (Android 13+) and accept the exact alarm prompt when redirected to settings.

## MIUI Optimisation Checklist
MIUI can aggressively kill background work. After installation:
1. Battery optimisation: tap **Request battery exception** from the in-app MIUI guide or navigate to Settings → Battery → choose Pomodoro Focus → set to **No restrictions**.
2. Autostart: Security app → Permissions → Autostart → enable Pomodoro Focus.
3. Recents lock: open recents, long-press Pomodoro Focus, and lock it.
4. Exact alarms & notifications: Settings → Notifications & Control Center → App notifications → Pomodoro Focus → enable all toggles, ensure exact alarms stay approved.

## Update Flow
1. Implement features in feature branches referencing issues.
2. Run `./gradlew test lint ktlintCheck` (if available) and `./gradlew assembleDebug` locally.
3. Create a PR with screenshots/GIFs for UI changes and note manual test steps.
4. Upon approval, merge and run the release pipeline:
   - Update changelog/notes.
   - Bump `version.properties` (code and optional semantic name).
   - Generate the signed universal APK.
   - Publish release notes and distribute sideload package as required.
