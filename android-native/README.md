# Native Android (Jetpack Compose) app

This repo contains an Expo app at the root, and a **native Android** implementation under `android-native/`.

## Open in Android Studio
- Android Studio → **Open** → select the `android-native/` folder.
- Wait for Gradle sync.
- Run the `app` configuration on an emulator/device.

## Features implemented (Compose)
- Single primary CTA: **FAB** for creating notes (no `+ New` in header)
- Header: **Notes** title + `X notes` subtitle (16dp padding)
- Search bar with in-field icon, rounded corners, improved surface contrast
- Notes: **2-column grid** (or list toggle), spacing, modern cards, pinned notes
- Persistence: **Room** (notes) + **DataStore** (view mode)

