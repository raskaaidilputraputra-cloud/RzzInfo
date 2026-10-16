package com.example.model

enum class VoucherCategory(val displayName: String, val brandTag: String) {
    SHOPEE("Shopee", "Marketplace"),
    LAZADA("Lazada", "Marketplace"),
    TOKOPEDIA("Tokopedia", "Marketplace"),
    FREE_FIRE("Free Fire", "Game"),
    MOBILE_LEGENDS("Mobile Legends", "Game")
}

enum class ItemStatus(val label: String) {
    ACTIVE("Aktif"),
    EXPIRING_SOON("Segera Berakhir"),
    EXPIRED("Kedaluwarsa")
}

data class VoucherItem(
    val id: String,
    val category: VoucherCategory,
    val title: String,
    val code: String,
    val benefit: String,
    val minSpend: String = "Rp 0",
    val maxDiscount: String = "-",
    val validUntil: String,
    val status: ItemStatus,
    val source: String,
    val terms: List<String>,
    val claimUrl: String,
    val dateAdded: String,
    val isVerified: Boolean = true,
    val badgeTag: String = "Resmi"
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String,
    val isAdmin: Boolean = false,
    val lastLoginTime: String,
    val deviceInfo: String
)

data class LoginHistoryItem(
    val id: String,
    val loginTime: String,
    val status: String = "Berhasil",
    val method: String = "Google Identity Services / OAuth 2.0",
    val device: String,
    val ipAddress: String = "182.253.14.72 (Indonesia - ISP Telekomunikasi)",
    val emailSent: Boolean = true
)

data class FaqItem(
    val id: Int,
    val question: String,
    val answer: String
)

data class DailyRefreshInfo(
    val lastRefreshTime: String,
    val lastCheckedDate: String,
    val newlyAddedCount: Int,
    val message: String
)
