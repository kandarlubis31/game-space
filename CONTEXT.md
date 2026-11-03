# game-space — Project Context

## Ringkasan

Aplikasi Android yang membatasi anak hanya bisa membuka aplikasi yang diizinkan. Daftar aplikasi
terpasang ditampilkan, lalu orang tua memilih mana yang masuk whitelist; sisanya tidak bisa dibuka
dari launcher aplikasi ini.

## Arsitektur

MVVM sederhana tanpa framework DI:

```
MainActivity / activity_main.xml      Daftar aplikasi + aksi whitelist
MainViewModel                        State dan pemanggilan repository
AppRepository                        Sumber data tunggal
AppManager                           Query PackageManager, baca/tulis whitelist
AppDatabase (Room)                   Penyimpanan whitelist
WhitelistDao / WhitelistEntity       Akses tabel whitelist
```

Keputusan penting: **whitelist disimpan di Room, bukan hanya di Preferences**, sehingga daftar bisa
dikueri dan diubah per paket. `AppManager` tetap menyediakan `loadWhitelist`/`saveWhitelist`
berbasis `Set<String>` sebagai jalur cepat untuk daftar paket.

`AppAdapter`/`AppItem` memisahkan data aplikasi dari tampilan daftar.

## Struktur

```
app/src/main/java/com/gamespace/
  MainActivity.java, MainViewModel.java, AppAdapter.java, AppItem.java, AppManager.java
  data/        Room: AppDatabase, WhitelistDao, WhitelistEntity
  repository/  AppRepository
app/src/main/res/layout/activity_main.xml
```

## Menjalankan

```bash
./gradlew assembleDebug      # APK debug
./gradlew installDebug       # pasang ke perangkat/emulator
```

## Catatan

- `QUERY_ALL_PACKAGES` (atau `queries` di manifest) diperlukan untuk melihat aplikasi terpasang di
  Android 11+.
- Aplikasi ini bukan launcher pengganti: pembatasan hanya berlaku selama anak masuk lewat aplikasi
  ini, bukan dari launcher bawaan.
