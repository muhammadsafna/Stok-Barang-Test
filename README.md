# Inventory Management App

Inventory Management App adalah aplikasi berbasis Android yang membantu pengguna dalam mengelola stok barang secara efisien. Aplikasi ini mengintegrasikan API untuk autentikasi dan pengambilan data, serta menggunakan database lokal Room untuk menyimpan data secara offline. Dengan arsitektur MVVM, aplikasi ini memberikan pengalaman pengguna yang responsif dan terstruktur.

## Fitur Utama

- **Autentikasi Pengguna**  
  Pengguna dapat login menggunakan email dan password. Sistem autentikasi menggunakan API yang mengembalikan token yang aman untuk akses selanjutnya.

- **Manajemen Data Item**  
  Aplikasi mendukung operasi CRUD (Create, Read, Update, Delete) untuk data item. Pengguna dapat menambahkan, mengedit, dan menghapus item dengan mudah.

- **Penyimpanan Lokal (Offline Support)**  
  Data item disimpan secara lokal menggunakan Room, sehingga data dapat diakses meskipun dalam keadaan offline. Data lokal akan diperbarui saat koneksi internet tersedia melalui proses sinkronisasi.

- **Integrasi API**  
  Aplikasi terhubung ke server melalui Retrofit. Endpoint API yang utama adalah untuk login dan mengambil data item. Token yang didapat saat login akan dikirim sebagai header otorisasi pada setiap permintaan data.

- **Pencarian dan Filter**  
  Pengguna dapat mencari item berdasarkan nama, jumlah stok, atau unit secara real time menggunakan fitur pencarian di halaman utama.

- **User Interface yang Ramah**  
  Dengan tampilan yang bersih dan responsif, aplikasi ini memudahkan pengguna dalam navigasi. Terdapat tampilan dialog untuk menambah dan mengedit item serta fitur drag & drop untuk Floating Action Button (FAB).

## Teknologi yang Digunakan

- **Kotlin**  
  Bahasa pemrograman utama untuk pengembangan aplikasi Android.

- **MVVM Architecture**  
  Memisahkan logika bisnis dari tampilan, sehingga kode menjadi lebih terstruktur dan mudah dikelola.

- **Retrofit**  
  Digunakan untuk berkomunikasi dengan API server.

- **Room Database**  
  Menyimpan data secara lokal dengan efisien, mendukung operasi CRUD.

- **LiveData & ViewModel**  
  Memastikan UI selalu sinkron dengan data terbaru secara reaktif.

- **Kotlin Coroutines**  
  Mengelola operasi asinkron dengan cara yang lebih sederhana dan efisien.

## Cara Menggunakan Aplikasi

1. **Login**  
   Buka aplikasi dan masukkan email serta password yang valid. Setelah login berhasil, token disimpan untuk autentikasi selanjutnya.

2. **Lihat Data Item**  
   Halaman utama akan menampilkan daftar item yang tersimpan di database lokal. Data ini dapat di-refresh secara manual melalui tombol refresh atau swipe refresh untuk mendapatkan data terbaru dari API.

3. **Tambah Item**  
   Tekan Floating Action Button (FAB) untuk membuka form dialog. Isi nama, jumlah stok, dan unit barang, kemudian tekan tombol simpan untuk menambahkan item ke database.

4. **Edit atau Hapus Item**  
   Setiap item yang ditampilkan memiliki tombol edit dan hapus. Tekan tombol edit untuk membuka form dengan data item yang sudah ada. Tekan tombol hapus untuk menghapus item dari database.

5. **Cari Item**  
   Gunakan kolom pencarian di bagian atas halaman utama untuk mencari item berdasarkan nama, jumlah stok, atau unit.

6. **Logout**  
   Tekan tombol logout untuk menghapus token dan kembali ke halaman login dengan aman.

## Kontak

Untuk informasi lebih lanjut atau diskusi, silakan hubungi saya di Instagram: [@muhmmadsf](https://www.instagram.com/muhmmadsf)
