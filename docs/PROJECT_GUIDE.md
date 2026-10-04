# CashPilot — технічний гайд

## Призначення

CashPilot — Android calendar/calculator + widget для відображення telemetry statistics і локального моделювання/редагування статусів днів.

## Основна модель

Є два шари:

```text
server cache
     +
local overrides
     ↓
rendered calendar/statistics
```

Server cache регулярно оновлюється незалежно від наявності local overrides.

Local override не видаляє server value, а перекриває його на rendering layer.

## Reset semantics

Reset selected month:
1. видаляє local overrides місяця;
2. запитує server data;
3. показує актуальний server state.

Він не є server reset.

## Widget

`CashPilotWidget`:
- працює з shared preferences;
- навігація місяців;
- toggle day;
- reset;
- auto-refresh;
- boot receiver.

Widget і MainActivity мають використовувати спільні keys/cache.

## Settings

Зберігаються:
- telemetry URL;
- token;
- install_id;
- optional stats URL;
- rates;
- theme та інше local UI state.

Secrets не входять у source/release defaults.

## UpdateManager

Manifest:
`releases/latest/download/update.json`.

Automatic checks мають interval; manual check обходить cadence.

Release manifest SHA-256 захищає download integrity.
Signing certificate захищає Android in-place update.

## Release workflow

`.github/workflows/android.yml`:
1. checkout;
2. JDK17;
3. decode permanent keystore;
4. fail if secrets missing;
5. assembleRelease;
6. copy `CashPilot-release.apk`;
7. SHA256;
8. generate `update.json`;
9. publish/overwrite assets under fixed `latest` tag.

Цей fixed-tag contract дозволяє client updater мати постійний URL.

## Версія

Фактичний source of truth на момент аудиту:
- versionName 1.9;
- versionCode 10.

README/build docs не повинні знижувати ці значення.

## Error behavior

При network failure:
- не знищуй valid cached month;
- не знищуй local overrides;
- UI має показати cached/local state, якщо доступно.

## Regression matrix

Перевірити:
- fresh install no credentials;
- configured server;
- invalid token;
- server down;
- month with no local edits;
- month with local edits;
- server data changes underneath local edit;
- reset local month;
- app/widget toggle same date;
- reboot widget;
- APK update preserves preferences.
