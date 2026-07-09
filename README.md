# 📦 Warehouse LegaPaket - Android Warehouse Management System

## 01. Nama & Deskripsi Project

**Warehouse LegaPaket** merupakan aplikasi **Warehouse Management System (WMS)** berbasis Android yang dikembangkan menggunakan bahasa pemrograman **Kotlin** sebagai proyek Ujian Akhir Semester (UAS) oleh **Kelompok 11**.

Aplikasi ini dirancang untuk membantu proses operasional gudang logistik mulai dari penerimaan barang (*Inbound*), pengelolaan data barang, penyortiran paket, pelacakan barang menggunakan **Barcode Scanner** berbasis kamera, manajemen perjalanan pengiriman (*Trip Management*), pelaporan insiden, hingga penyajian dashboard analitik operasional.

Fitur utama aplikasi adalah **Barcode Scanner**, yang memungkinkan petugas melakukan pemindaian barcode menggunakan kamera smartphone untuk mencari data resi secara otomatis dan memperbarui status paket tanpa perlu melakukan input manual.

Untuk penyimpanan data, aplikasi menggunakan **SharedPreferences** yang dipadukan dengan **Google GSON** sehingga seluruh data dapat disimpan dalam format JSON secara lokal tanpa memerlukan database server eksternal.

---

## 02. Daftar Anggota

**Kelompok 11**

| Nama Lengkap | NPM | Peran |
|--------------|------------|----------------------------------------------|
| **Revan Dwiki Juniarta** | **24552011206** | Full-Stack Android Developer (UI/UX Design, System Architecture, & Core Programmer) |

---

## 03. Link Video Penjelasan

Video demonstrasi aplikasi beserta penjelasan fitur dan source code dapat diakses melalui tautan berikut.

**Link YouTube**

```
https://youtu.be/sQxy0GGo3p4
```

---

## 04. Screenshot Aplikasi

### Dashboard Analitik

![Dashboard](docs/dashboard_warehouse_legapaket.jpeg)

---

### Barcode Scanner

![Barcode Scanner](docs/scanresi_warehouse_legapaket.jpeg)

---

### Manajemen Barang

![Manajemen Barang](docs/barang_warehouse_legapaket.jpeg)

---

### Riwayat Aktivitas & Laporan

![Laporan](docs/laporan_warehouse_legapaket.jpeg)

---

## 05. Cara Menjalankan Project

Project dapat dijalankan menggunakan dua cara, yaitu melalui file APK atau melalui Android Studio.

### Opsi A — Menjalankan Menggunakan File APK

1. Buka folder **apk** pada repository.
2. Install file **app-release.apk** pada perangkat Android.
3. Jika diminta, aktifkan izin instalasi dari **Sumber Tidak Dikenal (Unknown Sources)**.
4. Tunggu hingga proses instalasi selesai.
5. Jalankan aplikasi Warehouse LegaPaket.

---

### Opsi B — Menjalankan Menggunakan Android Studio

#### 1. Clone Repository

```bash
git clone https://github.com/revandwikij/Warehouse_Lega-Paket.git
```

#### 2. Masuk ke Folder Project

```bash
cd Warehouse_Lega-Paket/warehouse
```

#### 3. Buka Project

Buka **Android Studio**, kemudian pilih menu **Open** dan arahkan ke folder:

```
Warehouse_Lega-Paket/warehouse
```

#### 4. Sinkronisasi Gradle

Tunggu hingga proses **Gradle Sync** selesai tanpa error.

#### 5. Jalankan Aplikasi

Hubungkan perangkat Android menggunakan:

- USB Debugging, atau
- Wireless Debugging

Kemudian tekan tombol **Run ▶** atau gunakan shortcut:

```
Shift + F10
```

Tunggu hingga proses build selesai dan aplikasi akan otomatis terpasang pada perangkat Android.

---

### Struktur Repository

```
Warehouse_Lega-Paket
│
├── apk
│   └── app-release.apk
│
├── docs
│   ├── dashboard_warehouse_legapaket.jpeg
│   ├── scanresi_warehouse_legapaket.jpeg
│   ├── barang_warehouse_legapaket.jpeg
│   ├── laporan_warehouse_legapaket.jpeg
│   └── diagram/
│
├── warehouse
│   ├── app/
│   ├── gradle/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── gradlew
│   ├── gradlew.bat
│   └── gradle.properties
│
├── .gitignore
└── README.md
```

---

### Catatan

- Minimum Android yang didukung adalah **Android 12 (API Level 31)**.
- Project dikembangkan menggunakan **Android Studio** dengan bahasa pemrograman **Kotlin**.
- Barcode Scanner memanfaatkan kamera smartphone untuk melakukan pemindaian barcode secara langsung.
- Seluruh data aplikasi disimpan secara lokal menggunakan **SharedPreferences** dan **Google GSON**.# 📦 Warehouse LegaPaket - Android Warehouse Management System

## 01. Nama & Deskripsi Project

**Warehouse LegaPaket** merupakan aplikasi **Warehouse Management System (WMS)** berbasis Android yang dikembangkan menggunakan bahasa pemrograman **Kotlin** sebagai proyek Ujian Akhir Semester (UAS) oleh **Kelompok 11**.

Aplikasi ini dirancang untuk membantu proses operasional gudang logistik mulai dari penerimaan barang (*Inbound*), pengelolaan data barang, penyortiran paket, pelacakan barang menggunakan **Barcode Scanner** berbasis kamera, manajemen perjalanan pengiriman (*Trip Management*), pelaporan insiden, hingga penyajian dashboard analitik operasional.

Fitur utama aplikasi adalah **Barcode Scanner**, yang memungkinkan petugas melakukan pemindaian barcode menggunakan kamera smartphone untuk mencari data resi secara otomatis dan memperbarui status paket tanpa perlu melakukan input manual.

Untuk penyimpanan data, aplikasi menggunakan **SharedPreferences** yang dipadukan dengan **Google GSON** sehingga seluruh data dapat disimpan dalam format JSON secara lokal tanpa memerlukan database server eksternal.

---

## 02. Daftar Anggota

**Kelompok 11**

| Nama Lengkap | NPM | Peran |
|--------------|------------|----------------------------------------------|
| **Revan Dwiki Juniarta** | **24552011206** | Full-Stack Android Developer (UI/UX Design, System Architecture, & Core Programmer) |

---

## 03. Link Video Penjelasan

Video demonstrasi aplikasi beserta penjelasan fitur dan source code dapat diakses melalui tautan berikut.

**Link YouTube**

```
https://youtu.be/ISI_LINK_VIDEO_ANDA
```

---

## 04. Screenshot Aplikasi

### Dashboard Analitik

![Dashboard](docs/dashboard_warehouse_legapaket.jpeg)

---

### Barcode Scanner

![Barcode Scanner](docs/scanresi_warehouse_legapaket.jpeg)

---

### Manajemen Barang

![Manajemen Barang](docs/barang_warehouse_legapaket.jpeg)

---

### Riwayat Aktivitas & Laporan

![Laporan](docs/laporan_warehouse_legapaket.jpeg)

---

## 05. Cara Menjalankan Project

Project dapat dijalankan menggunakan dua cara, yaitu melalui file APK atau melalui Android Studio.

### Opsi A — Menjalankan Menggunakan File APK

1. Buka folder **apk** pada repository.
2. Install file **app-release.apk** pada perangkat Android.
3. Jika diminta, aktifkan izin instalasi dari **Sumber Tidak Dikenal (Unknown Sources)**.
4. Tunggu hingga proses instalasi selesai.
5. Jalankan aplikasi Warehouse LegaPaket.

---

### Opsi B — Menjalankan Menggunakan Android Studio

#### 1. Clone Repository

```bash
git clone https://github.com/revandwikij/Warehouse_Lega-Paket.git
```

#### 2. Masuk ke Folder Project

```bash
cd Warehouse_Lega-Paket/warehouse
```

#### 3. Buka Project

Buka **Android Studio**, kemudian pilih menu **Open** dan arahkan ke folder:

```
Warehouse_Lega-Paket/warehouse
```

#### 4. Sinkronisasi Gradle

Tunggu hingga proses **Gradle Sync** selesai tanpa error.

#### 5. Jalankan Aplikasi

Hubungkan perangkat Android menggunakan:

- USB Debugging, atau
- Wireless Debugging

Kemudian tekan tombol **Run ▶** atau gunakan shortcut:

```
Shift + F10
```

Tunggu hingga proses build selesai dan aplikasi akan otomatis terpasang pada perangkat Android.

---

### Struktur Repository

```
Warehouse_Lega-Paket
│
├── apk
│   └── app-release.apk
│
├── docs
│   ├── dashboard_warehouse_legapaket.jpeg
│   ├── scanresi_warehouse_legapaket.jpeg
│   ├── barang_warehouse_legapaket.jpeg
│   ├── laporan_warehouse_legapaket.jpeg
│   └── diagram/
│
├── warehouse
│   ├── app/
│   ├── gradle/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── gradlew
│   ├── gradlew.bat
│   └── gradle.properties
│
├── .gitignore
└── README.md
```

---

### Catatan

- Minimum Android yang didukung adalah **Android 12 (API Level 31)**.
- Project dikembangkan menggunakan **Android Studio** dengan bahasa pemrograman **Kotlin**.
- Barcode Scanner memanfaatkan kamera smartphone untuk melakukan pemindaian barcode secara langsung.
- Seluruh data aplikasi disimpan secara lokal menggunakan **SharedPreferences** dan **Google GSON**.
