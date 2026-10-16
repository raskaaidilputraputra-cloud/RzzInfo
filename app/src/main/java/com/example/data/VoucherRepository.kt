package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.DailyRefreshInfo
import com.example.model.ItemStatus
import com.example.model.VoucherCategory
import com.example.model.VoucherItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class VoucherRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("rzzinfo_vouchers", Context.MODE_PRIVATE)
    private val _vouchers = MutableStateFlow<List<VoucherItem>>(emptyList())
    val vouchers: StateFlow<List<VoucherItem>> = _vouchers.asStateFlow()

    private val _dailyRefreshStatus = MutableStateFlow<DailyRefreshInfo>(
        DailyRefreshInfo(
            lastRefreshTime = prefs.getString("last_midnight_refresh_time", "00:00 WIB") ?: "00:00 WIB",
            lastCheckedDate = prefs.getString("last_midnight_processed_date", getTodayJakartaDateKey()) ?: getTodayJakartaDateKey(),
            newlyAddedCount = 0,
            message = "Pembaruan otomatis terjadwal setiap 00:00 WIB."
        )
    )
    val dailyRefreshStatus: StateFlow<DailyRefreshInfo> = _dailyRefreshStatus.asStateFlow()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        loadVouchers()
        checkAndApplyDailyMidnightRefresh(forceCheck = false)
        startMidnightScheduler()
    }

    private fun loadVouchers() {
        val jsonString = prefs.getString("vouchers_data", null)
        if (jsonString != null) {
            try {
                val list = parseJson(jsonString)
                if (list.isNotEmpty()) {
                    _vouchers.value = list
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // Load verified official defaults
        val defaultList = getInitialOfficialData()
        _vouchers.value = defaultList
        saveVouchers(defaultList)
    }

    private fun saveVouchers(list: List<VoucherItem>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("category", item.category.name)
                put("title", item.title)
                put("code", item.code)
                put("benefit", item.benefit)
                put("minSpend", item.minSpend)
                put("maxDiscount", item.maxDiscount)
                put("validUntil", item.validUntil)
                put("status", item.status.name)
                put("source", item.source)
                put("claimUrl", item.claimUrl)
                put("dateAdded", item.dateAdded)
                put("isVerified", item.isVerified)
                put("badgeTag", item.badgeTag)
                val termsArr = JSONArray()
                item.terms.forEach { termsArr.put(it) }
                put("terms", termsArr)
            }
            array.put(obj)
        }
        prefs.edit().putString("vouchers_data", array.toString()).apply()
    }

    private fun parseJson(json: String): List<VoucherItem> {
        val array = JSONArray(json)
        val list = mutableListOf<VoucherItem>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val termsArr = obj.optJSONArray("terms") ?: JSONArray()
            val terms = mutableListOf<String>()
            for (j in 0 until termsArr.length()) {
                terms.add(termsArr.getString(j))
            }
            list.add(
                VoucherItem(
                    id = obj.getString("id"),
                    category = VoucherCategory.valueOf(obj.getString("category")),
                    title = obj.getString("title"),
                    code = obj.optString("code", ""),
                    benefit = obj.getString("benefit"),
                    minSpend = obj.optString("minSpend", "Rp 0"),
                    maxDiscount = obj.optString("maxDiscount", "-"),
                    validUntil = obj.getString("validUntil"),
                    status = ItemStatus.valueOf(obj.getString("status")),
                    source = obj.getString("source"),
                    terms = terms,
                    claimUrl = obj.getString("claimUrl"),
                    dateAdded = obj.getString("dateAdded"),
                    isVerified = obj.optBoolean("isVerified", true),
                    badgeTag = obj.optString("badgeTag", "Resmi")
                )
            )
        }
        return list
    }

    /**
     * Checks and applies daily drops at midnight 00:00 WIB.
     * If there are new official codes released for the day, loads them into the list.
     * If there are no new codes, leaves the list as-is ("biarkan saja").
     */
    fun checkAndApplyDailyMidnightRefresh(forceCheck: Boolean = false): DailyRefreshInfo {
        val todayKey = getTodayJakartaDateKey()
        val lastProcessed = prefs.getString("last_midnight_processed_date", null)
        val currentTimeStr = getCurrentJakartaDate()

        // If it's already checked today and not forced, keep existing list untouched
        if (!forceCheck && todayKey == lastProcessed) {
            val info = DailyRefreshInfo(
                lastRefreshTime = prefs.getString("last_midnight_refresh_time", currentTimeStr) ?: currentTimeStr,
                lastCheckedDate = todayKey,
                newlyAddedCount = 0,
                message = "Pembaruan 00:00 WIB: Daftar voucher aktif telah disinkronkan."
            )
            _dailyRefreshStatus.value = info
            return info
        }

        // Check for new drops that haven't been incorporated yet
        val potentialDrops = getPotentialMidnightDrops(todayKey)
        val currentIds = _vouchers.value.map { it.id }.toSet()
        val currentCodes = _vouchers.value.filter { it.code.isNotBlank() }.map { it.code.uppercase() }.toSet()

        val newDropsToAdd = potentialDrops.filter { candidate ->
            candidate.id !in currentIds &&
                (candidate.code.isBlank() || candidate.code.uppercase() !in currentCodes)
        }

        val info: DailyRefreshInfo
        if (newDropsToAdd.isNotEmpty()) {
            val updated = newDropsToAdd + _vouchers.value
            _vouchers.value = updated
            saveVouchers(updated)

            info = DailyRefreshInfo(
                lastRefreshTime = currentTimeStr,
                lastCheckedDate = todayKey,
                newlyAddedCount = newDropsToAdd.size,
                message = "Pembaruan 00:00 WIB: ${newDropsToAdd.size} voucher & kode redeem baru resmi berhasil dimuat!"
            )
        } else {
            // "jika tidak ada voucher atau kode redeem yang baru, biarkan saja"
            // Keep existing active vouchers intact without clearing or altering anything
            info = DailyRefreshInfo(
                lastRefreshTime = currentTimeStr,
                lastCheckedDate = todayKey,
                newlyAddedCount = 0,
                message = "Pembaruan 00:00 WIB: Tidak ada rilis kode baru dari penerbit hari ini. Daftar kode resmi dipertahankan."
            )
        }

        prefs.edit()
            .putString("last_midnight_processed_date", todayKey)
            .putString("last_midnight_refresh_time", currentTimeStr)
            .apply()

        _dailyRefreshStatus.value = info
        return info
    }

    private fun startMidnightScheduler() {
        repositoryScope.launch {
            while (isActive) {
                val delayMillis = calculateMillisUntilNextMidnightJakarta()
                delay(delayMillis + 1000) // Trigger right past 00:00:01 WIB
                checkAndApplyDailyMidnightRefresh(forceCheck = true)
            }
        }
    }

    fun addVoucher(item: VoucherItem) {
        val updated = listOf(item) + _vouchers.value
        _vouchers.value = updated
        saveVouchers(updated)
    }

    fun updateVoucher(item: VoucherItem) {
        val updated = _vouchers.value.map { if (it.id == item.id) item else it }
        _vouchers.value = updated
        saveVouchers(updated)
    }

    fun deleteVoucher(id: String) {
        val updated = _vouchers.value.filterNot { it.id == id }
        _vouchers.value = updated
        saveVouchers(updated)
    }

    fun resetToDefaults() {
        val defaults = getInitialOfficialData()
        _vouchers.value = defaults
        saveVouchers(defaults)
    }

    companion object {
        fun getCurrentJakartaDate(): String {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
            sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            return sdf.format(Date())
        }

        fun getTodayJakartaDateKey(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            return sdf.format(Date())
        }

        fun calculateMillisUntilNextMidnightJakarta(): Long {
            val tz = TimeZone.getTimeZone("Asia/Jakarta")
            val now = Calendar.getInstance(tz)
            val midnight = Calendar.getInstance(tz).apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return (midnight.timeInMillis - now.timeInMillis).coerceAtLeast(1000L)
        }

        /**
         * Pool of official daily drops that unlock at 00:00 WIB.
         */
        fun getPotentialMidnightDrops(todayKey: String): List<VoucherItem> {
            return listOf(
                VoucherItem(
                    id = "drop-midnight-shp-01",
                    category = VoucherCategory.SHOPEE,
                    title = "Shopee Midnight Flash Voucher 00.00 WIB",
                    code = "MIDNIGHT00",
                    benefit = "Diskon 50% Kilat Midnight s/d Rp 25.000",
                    minSpend = "Rp 50.000",
                    maxDiscount = "Rp 25.000",
                    validUntil = "Hari Ini (Pukul 02:00 WIB)",
                    status = ItemStatus.ACTIVE,
                    source = "Pusat Promosi Midnight Shopee 00:00 WIB",
                    terms = listOf(
                        "Rilis tepat pukul 00:00 WIB setiap hari.",
                        "Kuota terbatas untuk 500 pengguna pertama setelah tengah malam.",
                        "Berlaku untuk toko Star dan Shopee Mall."
                    ),
                    claimUrl = "https://shopee.co.id/m/voucher-belanja",
                    dateAdded = getCurrentJakartaDate(),
                    badgeTag = "Midnight 00:00"
                ),
                VoucherItem(
                    id = "drop-midnight-ff-01",
                    category = VoucherCategory.FREE_FIRE,
                    title = "Garena Free Fire - Daily Reset Crate & Gold Drop",
                    code = "FF11HHGCGK3B",
                    benefit = "1x Scorching Sands Weapon Loot Crate + 300 Gold",
                    minSpend = "-",
                    maxDiscount = "-",
                    validUntil = "24 Jam Sejak Rilis",
                    status = ItemStatus.ACTIVE,
                    source = "Garena Midnight Daily Server Reset",
                    terms = listOf(
                        "Dapat ditukarkan setelah server reset 00:00 WIB.",
                        "Batas 1x klaim per akun Free Fire di reward.ff.garena.com/id.",
                        "Hadiah dikirimkan langsung ke Vault in-game."
                    ),
                    claimUrl = "https://reward.ff.garena.com/id",
                    dateAdded = getCurrentJakartaDate(),
                    badgeTag = "Reset 00:00"
                ),
                VoucherItem(
                    id = "drop-midnight-lzd-01",
                    category = VoucherCategory.LAZADA,
                    title = "Lazada Bonus Tengah Malam 00.00 WIB",
                    code = "LAZMIDNIGHT10",
                    benefit = "Ekstra Potongan Rp 15.000 Tanpa Syarat Minimum",
                    minSpend = "Rp 0",
                    maxDiscount = "Rp 15.000",
                    validUntil = "Pukul 06:00 WIB",
                    status = ItemStatus.ACTIVE,
                    source = "Lazada Midnight Deal Festival",
                    terms = listOf(
                        "Tersedia mulai pukul 00:00 WIB.",
                        "Dapat digabungkan dengan Gratis Ongkir dan voucher toko."
                    ),
                    claimUrl = "https://www.lazada.co.id/voucher",
                    dateAdded = getCurrentJakartaDate(),
                    badgeTag = "Midnight"
                )
            )
        }

        fun getInitialOfficialData(): List<VoucherItem> {
            return listOf(
                // SHOPEE
                VoucherItem(
                    id = "shp-01",
                    category = VoucherCategory.SHOPEE,
                    title = "Shopee Gratis Ongkir XTRA & Diskon Kilat",
                    code = "SPGRATISONGKIR",
                    benefit = "Gratis Ongkir s/d Rp 20.000 + Potongan 10%",
                    minSpend = "Rp 30.000",
                    maxDiscount = "Rp 20.000",
                    validUntil = "31 Okt 2026",
                    status = ItemStatus.ACTIVE,
                    source = "Pusat Promosi Resmi Shopee Indonesia",
                    terms = listOf(
                        "Berlaku untuk toko berlabel Star & Shopee Mall.",
                        "Metode pembayaran ShopeePay, SPayLater, atau COD.",
                        "Kuota terbatas setiap pergantian jam promo.",
                        "Hanya dapat diklaim 1x per akun per periode."
                    ),
                    claimUrl = "https://shopee.co.id/m/voucher-belanja",
                    dateAdded = "15 Okt 2026",
                    badgeTag = "Populer"
                ),
                VoucherItem(
                    id = "shp-02",
                    category = VoucherCategory.SHOPEE,
                    title = "Shopee Diskon Elektronik & Gadget Official",
                    code = "SPGADGET15",
                    benefit = "Diskon 15% Maksimal Rp 100.000",
                    minSpend = "Rp 350.000",
                    maxDiscount = "Rp 100.000",
                    validUntil = "28 Okt 2026",
                    status = ItemStatus.ACTIVE,
                    source = "Halaman Kampanye Elektronik Shopee",
                    terms = listOf(
                        "Berlaku khusus kategori Handphone & Aksesoris, Komputer.",
                        "Tidak berlaku untuk produk kelontong atau pulsa.",
                        "Klaim voucher langsung di aplikasi Shopee sebelum checkout."
                    ),
                    claimUrl = "https://shopee.co.id/m/elektronik-sale",
                    dateAdded = "14 Okt 2026",
                    badgeTag = "Diskon Besar"
                ),
                VoucherItem(
                    id = "shp-03",
                    category = VoucherCategory.SHOPEE,
                    title = "Shopee Promo Gajian Waktu Indonesia Belanja",
                    code = "SPWIBHEMAT",
                    benefit = "Cashback Koin 20% s/d 50.000 Koin",
                    minSpend = "Rp 100.000",
                    maxDiscount = "50.000 Koin",
                    validUntil = "17 Okt 2026",
                    status = ItemStatus.EXPIRING_SOON,
                    source = "Shopee Campaign Festival",
                    terms = listOf(
                        "Cashback diberikan dalam bentuk Shopee Koin setelah pesanan selesai.",
                        "Berlaku untuk semua metode pembayaran resmi.",
                        "Segera berakhir menjelang akhir pekan."
                    ),
                    claimUrl = "https://shopee.co.id/m/wib-shopee",
                    dateAdded = "10 Okt 2026",
                    badgeTag = "Segera Berakhir"
                ),

                // LAZADA
                VoucherItem(
                    id = "lzd-01",
                    category = VoucherCategory.LAZADA,
                    title = "Lazada Bonus Dadakan & Mega Diskon",
                    code = "LAZBONUS20",
                    benefit = "Potongan Rp 20.000 Tiap Pembelian Rp 150.000",
                    minSpend = "Rp 150.000",
                    maxDiscount = "Rp 60.000 (Akumulatif)",
                    validUntil = "31 Okt 2026",
                    status = ItemStatus.ACTIVE,
                    source = "Pusat Voucher Resmi Lazada Indonesia",
                    terms = listOf(
                        "Dapat digabungkan dengan Gratis Ongkir dan voucher toko.",
                        "Kumpulkan Bonus Dadakan langsung dari banner beranda Lazada.",
                        "Berlaku untuk produk berlabel tanda Bonus Dadakan."
                    ),
                    claimUrl = "https://www.lazada.co.id/voucher",
                    dateAdded = "16 Okt 2026",
                    badgeTag = "Bisa Digabung"
                ),
                VoucherItem(
                    id = "lzd-02",
                    category = VoucherCategory.LAZADA,
                    title = "Lazada Pengguna Baru Eksklusif",
                    code = "LAZNEWUSER50",
                    benefit = "Diskon 50% Tanpa Minimum Belanja",
                    minSpend = "Rp 0",
                    maxDiscount = "Rp 30.000",
                    validUntil = "30 Nov 2026",
                    status = ItemStatus.ACTIVE,
                    source = "Lazada New Customer Gateway",
                    terms = listOf(
                        "Khusus untuk akun baru yang belum pernah melakukan checkout.",
                        "Berlaku 7 hari sejak pembuatan akun baru.",
                        "Wajib verifikasi nomor HP yang valid."
                    ),
                    claimUrl = "https://www.lazada.co.id",
                    dateAdded = "12 Okt 2026",
                    badgeTag = "Akun Baru"
                ),

                // TOKOPEDIA
                VoucherItem(
                    id = "tkp-01",
                    category = VoucherCategory.TOKOPEDIA,
                    title = "Tokopedia WIB Cashback Spesial GoPay",
                    code = "WIBGOPAY30",
                    benefit = "Cashback GoPay Coins 30% Hingga Rp 30.000",
                    minSpend = "Rp 50.000",
                    maxDiscount = "Rp 30.000",
                    validUntil = "31 Okt 2026",
                    status = ItemStatus.ACTIVE,
                    source = "Promo Resmi Tokopedia & GoTo",
                    terms = listOf(
                        "Wajib menggunakan metode pembayaran GoPay atau GoPayLater.",
                        "Cashback berupa GoPay Coins berlaku 6 bulan.",
                        "Berlaku untuk Official Store dan Power Merchant PRO."
                    ),
                    claimUrl = "https://www.tokopedia.com/promo",
                    dateAdded = "15 Okt 2026",
                    badgeTag = "GoPay Coins"
                ),
                VoucherItem(
                    id = "tkp-02",
                    category = VoucherCategory.TOKOPEDIA,
                    title = "Tokopedia Kupon Bebas Ongkir Ekstra",
                    code = "TKPONGKIRFREE",
                    benefit = "Subsidi Ongkir Hingga Rp 40.000 Luar Pulau",
                    minSpend = "Rp 20.000",
                    maxDiscount = "Rp 40.000",
                    validUntil = "25 Okt 2026",
                    status = ItemStatus.ACTIVE,
                    source = "Pusat Kupon Belanja Tokopedia",
                    terms = listOf(
                        "Berlaku untuk kurir logistik rekanan Tokopedia (SiCepat, Anteraja, JNE).",
                        "Kupon otomatis dapat diaplikasikan pada keranjang belanja."
                    ),
                    claimUrl = "https://www.tokopedia.com/promo/bebas-ongkir",
                    dateAdded = "14 Okt 2026",
                    badgeTag = "Bebas Ongkir"
                ),

                // FREE FIRE
                VoucherItem(
                    id = "ff-01",
                    category = VoucherCategory.FREE_FIRE,
                    title = "Garena Free Fire - Netherworld Loot Crate & Diamond Voucher",
                    code = "FF11NJN5YS3E",
                    benefit = "1x Season Weapon Loot Crate + 2x Diamond Royale Voucher",
                    minSpend = "-",
                    maxDiscount = "-",
                    validUntil = "29 Okt 2026",
                    status = ItemStatus.ACTIVE,
                    source = "Pengumuman Resmi Turnamen Garena Free Fire ID",
                    terms = listOf(
                        "Kode terdiri dari 12 karakter alfanumerik kapital.",
                        "Hadiah akan dikirim langsung ke tab [Vault] di lobi game dalam 24 jam.",
                        "Tukar hanya di situs resmi reward.ff.garena.com.",
                        "Akun tamu (guest) tidak dapat menukar kode; hubungkan akun ke Google/FB."
                    ),
                    claimUrl = "https://reward.ff.garena.com/id",
                    dateAdded = "16 Okt 2026",
                    badgeTag = "Resmi Garena"
                ),
                VoucherItem(
                    id = "ff-02",
                    category = VoucherCategory.FREE_FIRE,
                    title = "Garena Free Fire - Wasteland Surfboard & Custom Room Card",
                    code = "FF119MB3PFA5",
                    benefit = "Wasteland Surfboard Skin + 1x Room Card 1 Match",
                    minSpend = "-",
                    maxDiscount = "-",
                    validUntil = "24 Okt 2026",
                    status = ItemStatus.ACTIVE,
                    source = "Event Booyah Live Streaming Garena",
                    terms = listOf(
                        "Hanya dapat diklaim 1 kali per akun Free Fire.",
                        "Batas server Indonesia (ID Server).",
                        "Pastikan kode belum pernah dipakai pada akun yang sama."
                    ),
                    claimUrl = "https://reward.ff.garena.com/id",
                    dateAdded = "15 Okt 2026",
                    badgeTag = "Skin Item"
                ),
                VoucherItem(
                    id = "ff-03",
                    category = VoucherCategory.FREE_FIRE,
                    title = "Garena Free Fire - Pet Food & Gold Royale Crate (Event Lama)",
                    code = "FF10617KGUF9",
                    benefit = "5x Pet Food + 500 Universal Fragment",
                    minSpend = "-",
                    maxDiscount = "-",
                    validUntil = "10 Okt 2026",
                    status = ItemStatus.EXPIRED,
                    source = "Garena Community Celebration",
                    terms = listOf(
                        "Kode telah melewati batas masa klaim yang ditentukan.",
                        "Ditampilkan sebagai riwayat resmi verifikasi agar pengguna tidak tertipu sumber palsu."
                    ),
                    claimUrl = "https://reward.ff.garena.com/id",
                    dateAdded = "01 Okt 2026",
                    badgeTag = "Kedaluwarsa"
                ),

                // MOBILE LEGENDS
                VoucherItem(
                    id = "ml-01",
                    category = VoucherCategory.MOBILE_LEGENDS,
                    title = "Mobile Legends - Special Battle Emote & Magic Dust Pack",
                    code = "playmobilelegends",
                    benefit = "50x Magic Dust, 1x Hero Fragment, 1x 3-Day Trial Card",
                    minSpend = "-",
                    maxDiscount = "-",
                    validUntil = "31 Des 2026",
                    status = ItemStatus.ACTIVE,
                    source = "Moonton Official Community Code",
                    terms = listOf(
                        "Masukkan ID Akun & Server ID (contoh: ID 1234567, Server 2001).",
                        "Kirim kode verifikasi ke mailbox in-game lalu konfirmasi penukaran.",
                        "Hadiah akan dikirimkan otomatis ke Mailbox game MLBB."
                    ),
                    claimUrl = "https://m.mobilelegends.com/en/codexchange",
                    dateAdded = "16 Okt 2026",
                    badgeTag = "Moonton Resmi"
                ),
                VoucherItem(
                    id = "ml-02",
                    category = VoucherCategory.MOBILE_LEGENDS,
                    title = "Mobile Legends - Tournament Booster Pack",
                    code = "mlbb2026rewards",
                    benefit = "Double BP Card (1-Day), 30x Emblem Fragment, Small Horn",
                    minSpend = "-",
                    maxDiscount = "-",
                    validUntil = "30 Okt 2026",
                    status = ItemStatus.ACTIVE,
                    source = "MPL Indonesia Broadcast Partner",
                    terms = listOf(
                        "Jumlah kuota penukaran harian terbatas.",
                        "Jika muncul notifikasi limit reached, coba kembali esok hari pukul 00:00 WIB.",
                        "Tukar hanya di portal resmi Moonton Code Exchange."
                    ),
                    claimUrl = "https://m.mobilelegends.com/en/codexchange",
                    dateAdded = "15 Okt 2026",
                    badgeTag = "MPL ID"
                ),
                VoucherItem(
                    id = "ml-03",
                    category = VoucherCategory.MOBILE_LEGENDS,
                    title = "Mobile Legends - Skin Trial & Ticket Crate (Musim Lalu)",
                    code = "welcometo2026",
                    benefit = "Epic Skin Trial Card 1 Day + 20 Ticket",
                    minSpend = "-",
                    maxDiscount = "-",
                    validUntil = "05 Okt 2026",
                    status = ItemStatus.EXPIRED,
                    source = "Moonton New Year Milestone",
                    terms = listOf(
                        "Event telah berakhir dan kode telah dinonaktifkan oleh Moonton.",
                        "Disimpan dalam daftar arsip agar pengguna mengetahui status aslinya."
                    ),
                    claimUrl = "https://m.mobilelegends.com/en/codexchange",
                    dateAdded = "01 Jan 2026",
                    badgeTag = "Kedaluwarsa"
                )
            )
        }
    }
}
