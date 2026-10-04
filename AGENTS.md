# CashPilot — правила роботи

## MUST READ

1. `00_READ_FIRST.md`
2. `README.md`
3. `docs/PROJECT_GUIDE.md`
4. `.github/workflows/android.yml`
5. `app/build.gradle.kts`
6. `UpdateManager.java`
7. `CashPilotWidget.java`

## 1. Data model

Server telemetry = read-only source.

CashPilot stores:
- server cache;
- local manual overrides;
- app/widget settings;
- rates;
- update metadata.

Rendering combines server cache + local overrides.

Local override wins for that date, але background refresh server cache continues.

## 2. Never write manual status to server

Не додавай PUT/POST day-status sync лише тому, що "так логічніше".

Reset CashPilot:
- clears local overrides for selected month;
- refreshes server data;
- does NOT reset server-side overrides.

Це фундаментальний контракт.

## 3. App + widget share state

MainActivity і widget повинні бачити ті самі local overrides/cache.

Не заводити окремий widget database/state.

При зміні SharedPreferences keys потрібна migration/fallback.

## 4. Refresh

App і widget мають різний cadence.
Android може throttling background work.

Не створюй aggressive background service для 10-second refresh widget.

## 5. Telemetry credentials

URL/token/install_id належать користувачу.

Не:
- коміть real token;
- hardcode production install_id;
- логуй token;
- вшивай приватні налаштування конкретного користувача у release.

## 6. Version/signing

Source of truth:
`app/build.gradle.kts`.

На момент аудиту:
- versionName 1.9;
- versionCode 10.

Кожен release update:
- збільшити versionCode;
- не міняти applicationId;
- не міняти signing key.

## 7. CI signing secrets

Потрібні:
- `CASH_PILOT_KEYSTORE_BASE64`
- `CASH_PILOT_KEYSTORE_PASSWORD`
- `CASH_PILOT_KEY_ALIAS`
- `CASH_PILOT_KEY_PASSWORD`

Workflow навмисно падає без release signing. Не обходити це debug build.

## 8. Update contract

Fixed release tag:
`latest`.

Assets:
- `CashPilot-release.apk`
- `update.json`.

Manifest містить:
- versionCode;
- versionName;
- sha256;
- apkUrl;
- releaseUrl.

Client manifest URL:
`https://github.com/svtorovus-ai/CashPilot/releases/latest/download/update.json`.

Не перейменовуй assets без одночасної backward-compatible migration updater-а.

## 9. Update install

Android user confirms install.

Не намагайся робити silent/root install.

Перевір SHA-256 перед install і зберігай FileProvider/REQUEST_INSTALL_PACKAGES contract.

## 10. Build

Local debug:
`gradlew.bat assembleDebug`.

Release потребує stable signing env.

CI JDK17.

## 11. Regression

Перед release:
- current month;
- previous/next month;
- server refresh;
- server unavailable/cache;
- local override;
- local override survives refresh;
- reset month;
- widget/app consistency;
- rates/calculation;
- widget boot refresh;
- auto update check;
- manual update;
- install over existing signed app.

## 12. Не робити

- server writes for local overrides;
- clear all preferences on reset;
- discard cache just because one request failed;
- change applicationId;
- change signing key;
- publish unsigned update;
- change tag/asset names casually.
