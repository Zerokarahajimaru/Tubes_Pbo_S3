# Java Vending Machine Simulation

Aplikasi simulasi Vending Machine sederhana berbasis GUI yang dibangun menggunakan Java Swing dan Maven. Aplikasi ini mendemonstrasikan penerapan **Design Patterns**, **Generic Programming**, dan **Clean Code**.

## 📋 Fitur Utama
* **List Produk:** Menampilkan 10 data dummy makanan/minuman.
* **Sistem Pembayaran:** Menerima pecahan uang Rp5.000, Rp10.000, Rp20.000, dan Rp50.000.
* **Algoritma Kembalian:** Menghitung kembalian secara otomatis dengan pecahan terkecil (Greedy Algorithm).
* **GUI Interaktif:** Antarmuka pengguna visual menggunakan Java Swing.

## 🛠 Teknologi & Konsep
Proyek ini menerapkan konsep teknis berikut:
1.  **Build Tool:** Maven (Manajemen dependensi & build lifecycle).
2.  **Design Patterns:**
    * **Singleton:** `VendingEngine` (Memastikan hanya ada satu instance logika mesin).
    * **Observer:** `VendingUI` (UI otomatis terupdate saat state saldo berubah).
    * **Facade:** `VendingFacade` (Menyederhanakan akses UI ke logika bisnis yang rumit).
3.  **Generic Programming:** Class `Inventory<T>` untuk penyimpanan stok yang fleksibel.
4.  **Unit Testing:** JUnit 5.
5.  **Error Handling:** Validasi input uang dan stok menggunakan try-catch.

## 📂 Struktur Folder
Pastikan struktur folder proyek Anda seperti berikut agar Maven dapat berjalan:

```text
TUBES/
├── pom.xml                 <-- File konfigurasi Maven (WAJIB DI ROOT)
├── README.md               <-- File dokumentasi ini
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── vending/
    │               ├── Main.java          <-- Entry Point
    │               ├── core/              <-- Logika Bisnis (Singleton, Inventory)
    │               ├── model/             <-- Data Class (Product)
    │               ├── patterns/          <-- Facade & Observer Interface
    │               ├── ui/                <-- Tampilan (Swing)
    │               └── util/              <-- Konstanta
    └── test/
        └── java/
            └── com/
                └── vending/
                    └── VendingTest.java   <-- Unit Testing