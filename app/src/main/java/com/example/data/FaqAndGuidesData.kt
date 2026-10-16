package com.example.data

import com.example.model.FaqItem

object FaqAndGuidesData {
    val faqList = listOf(
        FaqItem(
            id = 1,
            question = "1. Apa itu RzzInfo?",
            answer = "RzzInfo adalah aplikasi dan portal pusat informasi resmi voucher marketplace (Shopee, Lazada, Tokopedia) dan kode redeem game populer (Free Fire, Mobile Legends: Bang Bang). RzzInfo menyajikan kode dan promosi terverifikasi langsung dari pengumuman resmi penerbit."
        ),
        FaqItem(
            id = 2,
            question = "2. Apakah voucher di RzzInfo resmi?",
            answer = "Ya, seluruh voucher dan kode redeem yang tertera di RzzInfo dihimpun dan diverifikasi langsung dari kanal resmi promosi marketplace dan event resmi pengembang game (Garena & Moonton). RzzInfo tidak pernah membagikan kode palsu atau generator acak."
        ),
        FaqItem(
            id = 3,
            question = "3. Bagaimana cara menggunakan voucher Shopee?",
            answer = "Buka aplikasi Shopee, klaim voucher melalui tautan resmi yang disediakan atau salin kode promo. Saat checkout produk di toko berlabel Star/Mall, masukkan atau pilih voucher pada kolom 'Voucher Shopee' sebelum melanjutkan pembayaran."
        ),
        FaqItem(
            id = 4,
            question = "4. Bagaimana cara menggunakan voucher Lazada?",
            answer = "Kumpulkan Bonus Dadakan atau salin voucher Lazada di RzzInfo. Masuk ke aplikasi Lazada, tambahkan produk bertanda Bonus Dadakan atau kategori terkait ke keranjang, dan potongan harga akan otomatis teraplikasikan pada ringkasan pesanan."
        ),
        FaqItem(
            id = 5,
            question = "5. Bagaimana cara menggunakan voucher Tokopedia?",
            answer = "Kunjungi halaman promo atau salin kode kupon Tokopedia. Pilih barang belanjaan dari Official Store atau Power Merchant, klik 'Gunakan Promo' pada halaman keranjang/checkout, lalu pastikan benefit cashback GoPay Coins atau diskon tertera."
        ),
        FaqItem(
            id = 6,
            question = "6. Bagaimana cara menukarkan kode redeem Free Fire?",
            answer = "Salin kode 12 karakter yang aktif di RzzInfo. Tekan tombol 'Klaim di Situs Resmi' untuk menuju https://reward.ff.garena.com/id. Login akun FF Anda (Google, Facebook, VK, Twitter, Apple ID), tempel kode, lalu klik Konfirmasi. Hadiah masuk ke tab Vault in-game."
        ),
        FaqItem(
            id = 7,
            question = "7. Bagaimana cara menukarkan kode redeem Mobile Legends?",
            answer = "Salin kode redeem MLBB. Buka situs penukaran resmi Moonton (m.mobilelegends.com/en/codexchange). Masukkan Redemption Code, Game ID Anda beserta Server ID, klik 'Send' untuk mendapatkan Verification Code di kotak pesan in-game, masukkan kode verifikasi tersebut lalu tekan 'Redeem'."
        ),
        FaqItem(
            id = 8,
            question = "8. Mengapa kode redeem tidak dapat digunakan?",
            answer = "Kode redeem game memiliki kuota penukaran terbatas (first-come, first-served), batasan server wilayah (region lock), atau batas kedaluwarsa. Jika batas kuota harian telah tercapai, sistem resmi game akan menolak penukaran."
        ),
        FaqItem(
            id = 9,
            question = "9. Apakah semua kode tersedia setiap hari?",
            answer = "Ketersediaan kode bergantung pada kampanye resmi, turnamen e-sports, dan event festival dari masing-masing penerbit. RzzInfo memperbarui daftar setiap kali ada kode baru yang dirilis secara sah."
        ),
        FaqItem(
            id = 10,
            question = "10. Apakah login Google aman?",
            answer = "Sangat aman. RzzInfo menggunakan Google Identity Services / OAuth 2.0 resmi. RzzInfo tidak pernah meminta atau menyimpan kata sandi Google Anda. Sesi diamankan di perangkat dan Anda dapat memantau riwayat login kapan saja."
        ),
        FaqItem(
            id = 11,
            question = "11. Bagaimana jika voucher sudah kedaluwarsa?",
            answer = "Voucher atau kode yang telah kedaluwarsa akan ditandai dengan badge merah 'Kedaluwarsa' dan tidak dapat digunakan lagi. Data arsip tetap ditampilkan secara transparan agar pengguna terhindar dari informasi palsu."
        ),
        FaqItem(
            id = 12,
            question = "12. Bagaimana cara menghubungi owner?",
            answer = "Anda dapat langsung menghubungi owner RzzInfo melalui WhatsApp resmi di nomor 0881022762735 (https://wa.me/62881022762735) melalui menu 'Hubungi Owner' di dalam aplikasi."
        )
    )

    val voucherGuideSteps = listOf(
        "Login terlebih dahulu ke RzzInfo menggunakan akun Google resmi.",
        "Pilih kategori marketplace yang diinginkan (Shopee, Lazada, atau Tokopedia).",
        "Cari voucher yang sesuai dengan kebutuhan belanja Anda melalui kolom pencarian.",
        "Baca secara teliti informasi diskon, minimum pembelian, dan syarat & ketentuan.",
        "Salin kode voucher jika tersedia dengan menekan tombol 'Salin Kode'.",
        "Buka aplikasi atau situs marketplace resmi melalui tombol yang disediakan.",
        "Masukkan atau klaim voucher sesuai prosedur masing-masing marketplace pada menu checkout.",
        "Pastikan potongan harga atau cashback telah teraplikasikan secara valid sebelum menyelesaikan transaksi pembayaran."
    )

    val ffGuideSteps = listOf(
        "Pilih kode redeem Free Fire yang berstatus aktif di aplikasi RzzInfo.",
        "Periksa deskripsi hadiah yang akan didapatkan serta masa berlakunya.",
        "Tekan tombol 'Salin Kode' untuk menyalin kode 12 karakter ke clipboard perangkat Anda.",
        "Tekan tombol 'Klaim Hadiah' untuk diarahkan langsung ke situs resmi Garena: reward.ff.garena.com/id.",
        "Login menggunakan akun Free Fire yang terikat (Google, Facebook, VK, Twitter, Huawei, atau Apple). Akun guest tidak didukung oleh Garena.",
        "Tempel kode redeem pada kotak isian yang tersedia, lalu tekan tombol 'Konfirmasi'.",
        "Buka aplikasi Free Fire dan periksa hadiah Anda di tab [Vault] atau [In-Game Mail] dalam waktu 30 menit hingga 24 jam."
    )

    val mlbbGuideSteps = listOf(
        "Pilih kode redeem Mobile Legends: Bang Bang yang aktif di RzzInfo.",
        "Baca syarat penukaran serta hadiah yang akan diperoleh.",
        "Salin kode redeem yang tertera.",
        "Tekan tombol 'Buka Penukaran Resmi' untuk menuju portal Moonton: m.mobilelegends.com/en/codexchange.",
        "Isi data yang diperlukan: Kode Redeem, Game ID Anda (beserta nomor Server dalam kurung).",
        "Tekan tombol 'Send' pada Verification Code untuk menerima kode angka 6 digit di kotak pesan (Mail) game MLBB Anda.",
        "Buka game MLBB, salin kode verifikasi dari Mail, kembali ke situs Moonton, masukkan kode tersebut, dan klik 'Redeem'. Hadiah langsung masuk ke Mail in-game."
    )

    val termsContent = """
1. Layanan Informasi Independen
RzzInfo merupakan aplikasi pusat informasi kurasi voucher promosi dan kode redeem yang dihimpun dari sumber terbuka dan resmi. RzzInfo bukan merupakan bagian dari, terafiliasi dengan, atau didukung secara langsung oleh Shopee, Lazada, Tokopedia, Garena Free Fire, maupun Moonton Mobile Legends.

2. Ketersediaan dan Kuota
Seluruh nilai diskon, kuota pemakaian, minimum transaksi, ketersediaan hadiah, dan batas masa aktif sepenuhnya merupakan hak dan kebijakan masing-masing penerbit resmi (Marketplace & Game Publisher). RzzInfo tidak menjamin bahwa setiap kode pasti berhasil digunakan untuk setiap akun, mengingat adanya pembatasan wilayah (region), batas pengguna baru, atau kuota kupon harian.

3. Kebijakan Privasi & Keamanan Akun
Autentikasi akun di RzzInfo menggunakan Google Identity Services / OAuth 2.0. RzzInfo tidak pernah meminta, mengakses, atau menyimpan kata sandi Google, kata sandi marketplace, OTP, maupun kredensial game Anda. Data login disimpan secara aman di perangkat lokal Anda.

4. Batasan Tanggung Jawab
RzzInfo tidak bertanggung jawab atas kegagalan transaksi pada situs pihak ketiga, perubahan syarat sepihak oleh marketplace, atau penutupan event game oleh publisher. Pengguna disarankan selalu membaca syarat dan ketentuan resmi pada masing-masing platform.
    """.trimIndent()
}
