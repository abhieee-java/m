# MyDialer

Minimal black-theme Android dialer. Java only, standard Views (no Compose).

## Run

1. Open Android Studio → **Open** → select the `android-mydialer/` folder.
2. Let Gradle sync (Android Gradle Plugin 8.5.2, compileSdk 34, minSdk 26 = Android 8+).
3. Plug in a device or start an emulator → press **Run ▶**.

> Android Studio downloads the Gradle wrapper automatically on first sync. If you prefer CLI, run `gradle wrapper` once in this folder, then `./gradlew assembleDebug`.

## Features

- 4 × 3 dial pad (0–9, *, #) with large touch targets and pressed states
- Number display with backspace (long-press to clear all)
- Red call FAB → `Intent.ACTION_CALL` with runtime `CALL_PHONE` permission
- Recent calls list backed by `SharedPreferences` (up to 20 entries)
  - Tap a recent → fills the display and redials
  - Most recent first; duplicates are de-duplicated and bumped to the top
- Adaptive launcher icon (black background, red phone glyph)
- Dark theme, edge-to-edge background

## Permissions

Only `CALL_PHONE`. No internet, no contacts, no analytics.

## Notes

- The standard Android emulator has no SIM, so `ACTION_CALL` will fail there. To test on the emulator, swap `Intent.ACTION_CALL` for `Intent.ACTION_DIAL` in `MainActivity#placeCall()` (no permission needed; opens the system dialer prefilled).
- Recents are stored in `SharedPreferences` (`mydialer_prefs.xml`) and survive app restarts. Backup rules opt this into Auto Backup and Device-to-Device transfer on Android 12+.
