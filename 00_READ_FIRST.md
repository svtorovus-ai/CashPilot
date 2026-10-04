# READ FIRST — CashPilot

Перед змінами прочитай `AGENTS.md` і `docs/PROJECT_GUIDE.md`.

## Головний контракт даних

CashPilot ЧИТАЄ telemetry server.

Ручні зміни дня в app/widget є ЛОКАЛЬНИМИ overrides цього пристрою.

Не перетворюй local override на server write. Це змінить продукт.

## Release identity

- applicationId: `ua.cashpilot`
- version source: `app/build.gradle.kts`
- на момент аудиту: versionName 1.9, versionCode 10
- release tag: `latest`
- assets:
  - `CashPilot-release.apk`
  - `update.json`

Update APK завжди має бути підписаний тим самим release key.

Не публікуй debug/unsigned artifact як update.
