# game-space

An Android app that turns a phone into a distraction-free gaming device by disabling every
app that is not on your whitelist.

Instead of a third-party "game booster" that only clears memory, this actually suspends
non-whitelisted packages for as long as the session is active, then restores them when you
switch it off.

## How it works

- `AppManager` enumerates installed packages and disables everything outside the whitelist,
  writing the affected package names to `disabled.txt` in app storage so the session can be
  rolled back exactly.
- A hardcoded **fatal system list** (`systemui`, `com.android.phone`, `settings`,
  Play Services, WebView, the permission controller, the shell) is never touched. Disabling
  those would brick the device.
- The whitelist is persisted in a Room database (`WhitelistEntity`, `WhitelistDao`,
  `AppDatabase`).
- `MainActivity` exposes a toggle, a status line, a progress indicator while packages are
  being processed, and a grid of installed apps for editing the whitelist.
- `AppRepository` sits between the UI and the package manager / database, reporting progress
  through a `GameModeCallback`.

## Privileges

Disabling other apps requires privileges a normal app does not have. This project uses
**Shizuku** (`rikka.shizuku`), which grants shell-level access through ADB without root.

You need the Shizuku app installed and its service started before the toggle will work.
Without Shizuku, the app can still browse and edit the whitelist, but cannot disable
anything.

## Build

Open the project in Android Studio, or from the command line:

```bash
./gradlew assembleDebug
```

## Structure

```
app/src/main/java/com/gamespace/
  MainActivity.java          toggle, status, app grid
  AppManager.java            enable/disable packages, fatal system list, rollback log
  AppAdapter.java            grid adapter
  AppItem.java               app model
  data/                      Room database, DAO, entity
  repository/                AppRepository + GameModeCallback
  viewmodel/                 MainViewModel
```

## Safety

Disabling a package you need can leave the device in a state where you cannot open settings
or launcher. The fatal system list guards the most dangerous ones, but keep a way to restore
packages (fastboot, recovery, or a second device) before experimenting. `disabled.txt`
records exactly what was turned off so a session can be undone.

## License

MIT
