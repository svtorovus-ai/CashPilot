# CashPilot

Android app and home-screen widget for reading PITUSHNYA MCC telemetry statistics.

## Statistics and cache rules

- Telemetry is read from the configured server. CashPilot never writes day statuses back to the server.
- A tap in the app or widget is a local override shared by both surfaces on the same device.
- Background refresh continues even when local overrides exist. Fresh server data replaces only the server cache; local overrides remain on top for their dates.
- Reset in CashPilot removes local overrides for the selected month and immediately downloads the current server data again.
- A manual status changed on the server remains a server-side override until the server statistics page is reset. After a CashPilot reset, that server-side value is downloaded normally.
- The app refreshes the open month about every 10 seconds. The widget refreshes about every 5 minutes, subject to Android background limits.

## Releases and in-app updates

The `main` workflow builds a signed release APK and publishes two assets to the `latest` GitHub release:

- `CashPilot-release.apk`
- `update.json`

CashPilot checks `update.json` automatically on launch/resume and exposes a manual check in Settings. The user confirms the Android installation; Android never allows a silent APK replacement.

### Required GitHub Actions secrets

The signing key must be created once and kept permanently. Never commit it to this repository.

- `CASH_PILOT_KEYSTORE_BASE64`: base64 contents of the `.jks` file
- `CASH_PILOT_KEYSTORE_PASSWORD`
- `CASH_PILOT_KEY_ALIAS`
- `CASH_PILOT_KEY_PASSWORD`

Every future release must use the same key, otherwise Android will reject an update over an installed build. Increase `versionCode` in `app/build.gradle.kts` for every release.

The current workflow intentionally fails when signing secrets are missing. Publishing an unsigned or ephemeral-debug APK would make in-place updates unreliable.

## Build locally

```text
gradlew.bat assembleDebug
```

For a release build, set the four signing environment variables used by `app/build.gradle.kts` and run `gradlew.bat assembleRelease`.
