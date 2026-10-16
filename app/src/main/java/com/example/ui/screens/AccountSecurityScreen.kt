package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthRepository
import com.example.ui.components.SkeuoBadge
import com.example.ui.components.SkeuoButton
import com.example.ui.components.SkeuoCard
import com.example.ui.theme.*

@Composable
fun AccountSecurityScreen(
    authRepository: AuthRepository,
    onNavigateToAdminDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by authRepository.currentUser.collectAsState()
    val history by authRepository.loginHistory.collectAsState()
    val emailStatus by authRepository.lastSentEmailStatus.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(SkyBlue100, SkyBlue50, PureWhite)
                )
            ),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Profil & Keamanan Akun",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = SkyBlue900
                        )
                        Text(
                            text = "Autentikasi resmi Google Identity & riwayat login.",
                            fontSize = 12.sp,
                            color = SlateMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = SkyBlue600,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Profile Card
        item {
            val user = currentUser
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Badge
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .shadow(4.dp, CircleShape)
                                    .background(
                                        brush = Brush.verticalGradient(listOf(SkyBlue500, SkyBlue700)),
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user?.name?.firstOrNull()?.toString()?.uppercase() ?: "U",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    color = PureWhite
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user?.name ?: "Pengguna Google",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = SlateText
                                    )
                                    if (user?.isAdmin == true) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        SkeuoBadge(
                                            text = "Admin",
                                            badgeColor = SkyBlue600,
                                            textColor = SkyBlue900
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = user?.email ?: "email@gmail.com",
                                    fontSize = 13.sp,
                                    color = SlateMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = SkyBlue100)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Device & Last Login
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = SkyBlue600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Perangkat: ${user?.deviceInfo ?: AuthRepository.getDeviceName()}",
                                fontSize = 12.sp,
                                color = SlateText
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = StatusActive,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Login Terakhir: ${user?.lastLoginTime ?: "Baru saja"}",
                                fontSize = 12.sp,
                                color = SlateText
                            )
                        }
                    }
                }
            }
        }

        // Email Notification Feature Card
        item {
            val user = currentUser
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = SkyBlue700,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Notifikasi Email Login Resmi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SlateText
                                )
                            }

                            SkeuoBadge(
                                text = "Otomatis ke Gmail",
                                badgeColor = StatusActive,
                                textColor = StatusActive
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = emailStatus ?: "Pesan keamanan resmi dikirimkan secara otomatis ke akun Gmail (${user?.email ?: "Google"}) setiap kali sesi login baru berhasil dibuat.",
                            fontSize = 12.sp,
                            color = if (emailStatus != null) StatusActive else SlateMuted,
                            lineHeight = 17.sp,
                            fontWeight = if (emailStatus != null) FontWeight.SemiBold else FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Email Content Preview Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SkyBlue200, RoundedCornerShape(10.dp))
                                .background(SkyBlue50, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Subjek: Anda telah login di RzzInfo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = SkyBlue800
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Halo, akun Google Anda baru saja digunakan untuk login ke RzzInfo.\n\nWaktu login: ${user?.lastLoginTime ?: "WIB"}\n\nJika Anda merasa tidak melakukan login ini, segera periksa keamanan akun Google Anda.\n\nTim RzzInfo",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SlateText,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        SkeuoButton(
                            text = "Buka / Kirim Ulang ke Gmail",
                            onClick = {
                                if (user != null) {
                                    val sent = authRepository.sendAutomaticLoginNotificationEmail(
                                        context = context,
                                        userEmail = user.email,
                                        loginTime = user.lastLoginTime
                                    )
                                    Toast.makeText(
                                        context,
                                        "Pesan notifikasi keamanan disiapkan ke Gmail (${user.email}).",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            icon = Icons.Default.Send,
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = true
                        )
                    }
                }
            }
        }

        // Administrator Gateway Card
        item {
            val user = currentUser
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = if (user?.isAdmin == true) SkyBlue700 else SlateMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Dashboard Administrator",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SlateText
                                )
                            }

                            if (user?.isAdmin == true) {
                                SkeuoBadge(
                                    text = "Aktif",
                                    badgeColor = StatusActive,
                                    textColor = StatusActive
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (user?.isAdmin == true) {
                                "Anda memiliki hak akses administrator. Anda dapat menambah, mengedit, dan memverifikasi voucher serta kode redeem resmi."
                            } else {
                                "Dashboard admin hanya dapat diakses oleh administrator terdaftar (raskaaidilputraputra@gmail.com) atau dengan Master PIN Admin."
                            },
                            fontSize = 12.sp,
                            color = SlateMuted,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (user?.isAdmin == true) {
                            SkeuoButton(
                                text = "Buka Dashboard Admin",
                                onClick = onNavigateToAdminDashboard,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_open_admin_dashboard",
                                isPrimary = true
                            )
                        } else {
                            SkeuoButton(
                                text = "Verifikasi Akses Admin (PIN)",
                                onClick = { showPinDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                isPrimary = false
                            )
                        }
                    }
                }
            }
        }

        // Login History Section
        item {
            Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = SkyBlue700,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Riwayat Keamanan Sesi Login",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = SkyBlue900
                    )
                }
            }
        }

        if (history.isEmpty()) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Belum ada riwayat tercatat.",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                }
            }
        } else {
            items(history, key = { it.id }) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    SkeuoCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.loginTime,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SlateText
                                )
                                SkeuoBadge(
                                    text = item.status,
                                    badgeColor = StatusActive,
                                    textColor = StatusActive
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Metode: ${item.method}",
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                            Text(
                                text = "Perangkat: ${item.device}",
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                            Text(
                                text = "Jaringan: ${item.ipAddress}",
                                fontSize = 11.sp,
                                color = SkyBlue700
                            )
                        }
                    }
                }
            }
        }

        // Logout Button
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                SkeuoButton(
                    text = "Keluar dari Akun (Logout)",
                    onClick = {
                        authRepository.logout()
                        Toast.makeText(context, "Sesi login ditutup.", Toast.LENGTH_SHORT).show()
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_logout",
                    icon = Icons.Default.Logout,
                    isPrimary = false,
                    contentColor = StatusExpired
                )
            }
        }
    }

    // PIN Dialog for Admin Access
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                enteredPin = ""
                pinError = false
            },
            title = {
                Text(
                    text = "Verifikasi Administrator",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Masukkan Master PIN Admin RzzInfo (Default: 881022) untuk mengaktifkan mode admin.",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            enteredPin = it
                            pinError = false
                        },
                        label = { Text("PIN Admin (6 digit)") },
                        singleLine = true,
                        isError = pinError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (pinError) {
                        Text(
                            text = "PIN salah. Silakan coba kembali.",
                            color = StatusExpired,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (enteredPin.trim() == "881022" || enteredPin.trim() == "62881022762735") {
                            authRepository.toggleAdminAccess(true)
                            showPinDialog = false
                            Toast.makeText(context, "Akses Administrator Diaktifkan!", Toast.LENGTH_SHORT).show()
                        } else {
                            pinError = true
                        }
                    }
                ) {
                    Text("Konfirmasi", fontWeight = FontWeight.Bold, color = SkyBlue700)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Batal", color = SlateMuted)
                }
            }
        )
    }
}
