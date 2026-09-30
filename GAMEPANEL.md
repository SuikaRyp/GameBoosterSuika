# Game Booster Panel (edge swipe)

Paket: `com.example.gamepanel`. Panel lama (`FloatingPanelManager`) TIDAK diubah dan tetap jalan.

## Alur
`GameBoostService` → `GamePanelController.onGameChanged(pkg)` (dari `repository.simulatedGame`) →
terapkan profil game → `EdgeGuards` (strip tipis di tepi kanan) → swipe kanan→kiri → `PanelWindow` (slide-in)
→ tombol `—` → `BubbleOverlay`. Tidak ada jendela overlay sama sekali saat tidak ada game aktif.

## Yang benar-benar bekerja tanpa root
Edge swipe, panel + animasi, HUD, bubble, kunci sentuh, proteksi tepi/atas-bawah, crosshair, catatan/timer/stopwatch/kalkulator,
RAM/baterai/layar/jaringan (API resmi), ping/loss/download/upload, DND & blokir notifikasi (NotificationManager),
kecerahan & kunci rotasi (WRITE_SETTINGS), volume/mute, rekam layar (MediaProjection), screenshot (aksi global Aksesibilitas, Android 9+),
tolak panggilan (peran Call Screening, Android 10+), profil per-game (SharedPreferences), daftar game (Room lama).

## Butuh Shizuku (jika tidak: tampil "N/A", tidak ada angka palsu)
FPS game (dumpsys SurfaceFlinger), CPU usage (Android 8+ memblokir /proc/stat), GPU usage/frekuensi, frekuensi CPU, visualisasi sentuhan.

## Batasan yang disengaja
- Sentuhan yang jatuh tepat di strip tepi tidak diteruskan ke game (batas overlay Android).
- Wi-Fi/Bluetooth: Android 10+ tidak mengizinkan app mengubahnya → membuka panel/pengaturan sistem.
- Sampling rate touch hardware & governor CPU tidak disentuh.
- "Prioritas game" hanya menjeda aktivitas internal app (antivirus latar belakang); proses lain tidak dimatikan.
