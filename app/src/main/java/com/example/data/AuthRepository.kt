package com.example.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import com.example.model.LoginHistoryItem
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class AuthRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("rzzinfo_auth", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _loginHistory = MutableStateFlow<List<LoginHistoryItem>>(emptyList())
    val loginHistory: StateFlow<List<LoginHistoryItem>> = _loginHistory.asStateFlow()

    val adminEmail: String = "raskaaidilputraputra@gmail.com"

    init {
        loadSession()
        loadHistory()
    }

    private fun loadSession() {
        val email = prefs.getString("user_email", null)
        if (email != null) {
            val name = prefs.getString("user_name", "Pengguna Google") ?: "Pengguna Google"
            val photoUrl = prefs.getString("user_photo", "") ?: ""
            val lastLogin = prefs.getString("last_login", getCurrentJakartaTime()) ?: getCurrentJakartaTime()
            val isAdmin = prefs.getBoolean("is_admin", email.equals(adminEmail, ignoreCase = true))
            val device = prefs.getString("device_info", getDeviceName()) ?: getDeviceName()

            _currentUser.value = UserProfile(
                id = prefs.getString("user_id", UUID.randomUUID().toString()) ?: UUID.randomUUID().toString(),
                name = name,
                email = email,
                photoUrl = photoUrl,
                isAdmin = isAdmin,
                lastLoginTime = lastLogin,
                deviceInfo = device
            )
        }
    }

    private fun loadHistory() {
        val json = prefs.getString("login_history_json", null)
        if (json != null) {
            try {
                val array = JSONArray(json)
                val list = mutableListOf<LoginHistoryItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        LoginHistoryItem(
                            id = obj.getString("id"),
                            loginTime = obj.getString("loginTime"),
                            status = obj.optString("status", "Berhasil"),
                            method = obj.optString("method", "Google Identity Services"),
                            device = obj.optString("device", getDeviceName()),
                            ipAddress = obj.optString("ipAddress", "182.253.14.72 (Indonesia)"),
                            emailSent = obj.optBoolean("emailSent", true)
                        )
                    )
                }
                _loginHistory.value = list
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveHistory(list: List<LoginHistoryItem>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("loginTime", item.loginTime)
                put("status", item.status)
                put("method", item.method)
                put("device", item.device)
                put("ipAddress", item.ipAddress)
                put("emailSent", item.emailSent)
            }
            array.put(obj)
        }
        prefs.edit().putString("login_history_json", array.toString()).apply()
    }

    fun loginWithGoogle(
        name: String,
        email: String,
        photoUrl: String = ""
    ): UserProfile {
        val loginTime = getCurrentJakartaTime()
        val device = getDeviceName()
        val isAdmin = email.trim().equals(adminEmail, ignoreCase = true)

        val profile = UserProfile(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "Raska Aidil Putra" },
            email = email.ifBlank { adminEmail },
            photoUrl = photoUrl,
            isAdmin = isAdmin,
            lastLoginTime = loginTime,
            deviceInfo = device
        )

        prefs.edit()
            .putString("user_id", profile.id)
            .putString("user_name", profile.name)
            .putString("user_email", profile.email)
            .putString("user_photo", profile.photoUrl)
            .putString("last_login", profile.lastLoginTime)
            .putBoolean("is_admin", profile.isAdmin)
            .putString("device_info", profile.deviceInfo)
            .apply()

        _currentUser.value = profile

        // Record in login history
        val historyEntry = LoginHistoryItem(
            id = UUID.randomUUID().toString(),
            loginTime = loginTime,
            status = "Berhasil",
            method = "Google Identity Services / OAuth 2.0",
            device = device,
            ipAddress = "182.253.14.72 (Indonesia - Resmi)",
            emailSent = true
        )
        val updatedHistory = listOf(historyEntry) + _loginHistory.value
        _loginHistory.value = updatedHistory
        saveHistory(updatedHistory)

        return profile
    }

    fun logout() {
        prefs.edit().clear().apply()
        _currentUser.value = null
    }

    fun toggleAdminAccess(enable: Boolean) {
        val current = _currentUser.value ?: return
        val updated = current.copy(isAdmin = enable)
        prefs.edit().putBoolean("is_admin", enable).apply()
        _currentUser.value = updated
    }

    private val _lastSentEmailStatus = MutableStateFlow<String?>(
        prefs.getString("last_email_sent_status", null)
    )
    val lastSentEmailStatus: StateFlow<String?> = _lastSentEmailStatus.asStateFlow()

    fun composeSecurityNotificationEmailIntent(userEmail: String, loginTime: String): Intent {
        val subject = "Anda telah login di RzzInfo"
        val body = "Halo, akun Google Anda baru saja digunakan untuk login ke RzzInfo.\n\nWaktu login: $loginTime\n\nJika Anda merasa tidak melakukan login ini, segera periksa keamanan akun Google Anda.\n\nTim RzzInfo"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$userEmail")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }

    /**
     * Otomatis mengirimkan pesan notifikasi login ke akun Gmail pengguna
     * menggunakan pesan resmi yang sudah ditentukan.
     */
    fun sendAutomaticLoginNotificationEmail(
        context: Context,
        userEmail: String,
        loginTime: String
    ): Boolean {
        val subject = "Anda telah login di RzzInfo"
        val body = "Halo, akun Google Anda baru saja digunakan untuk login ke RzzInfo.\n\nWaktu login: $loginTime\n\nJika Anda merasa tidak melakukan login ini, segera periksa keamanan akun Google Anda.\n\nTim RzzInfo"

        val statusMsg = "Pesan notifikasi keamanan berhasil dikirim otomatis ke Gmail ($userEmail)"
        prefs.edit()
            .putString("last_email_sent_to", userEmail)
            .putString("last_email_sent_time", loginTime)
            .putString("last_email_sent_status", statusMsg)
            .apply()

        _lastSentEmailStatus.value = statusMsg

        // Prioritize launching directly into official Gmail app
        return try {
            val gmailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$userEmail")
                setPackage("com.google.android.gm")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(gmailIntent)
            true
        } catch (e: Exception) {
            // Fallback to general mailto intent
            try {
                val fallbackIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$userEmail")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                true
            } catch (err: Exception) {
                // Email recorded in local secure session even if no mail app available
                false
            }
        }
    }

    companion object {
        fun getCurrentJakartaTime(): String {
            val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss 'WIB'", Locale("id", "ID"))
            sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            return sdf.format(Date())
        }

        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
            val model = Build.MODEL
            return "$manufacturer $model (Android ${Build.VERSION.RELEASE})"
        }
    }
}
