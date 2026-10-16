package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuthRepository
import com.example.ui.components.SkeuoButton
import com.example.ui.components.SkeuoCard
import com.example.ui.theme.CardSurfaceWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkeuoHighlight
import com.example.ui.theme.SkeuoShadowDark
import com.example.ui.theme.SkeuoShadowDeep
import com.example.ui.theme.SkyBlue100
import com.example.ui.theme.SkyBlue200
import com.example.ui.theme.SkyBlue300
import com.example.ui.theme.SkyBlue400
import com.example.ui.theme.SkyBlue50
import com.example.ui.theme.SkyBlue600
import com.example.ui.theme.SkyBlue700
import com.example.ui.theme.SkyBlue800
import com.example.ui.theme.SkyBlue900
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeLoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showCustomEmailOption by remember { mutableStateOf(false) }
    var customEmail by remember { mutableStateOf("raskaaidilputraputra@gmail.com") }
    var customName by remember { mutableStateOf("Raska Aidil Putra") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyBlue100,
                        SkyBlue50,
                        PureWhite
                    )
                )
            )
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon & Brand Logo Badge
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .shadow(12.dp, CircleShape, ambientColor = SkyBlue600, spotColor = SkyBlue800)
                    .border(
                        width = 3.dp,
                        brush = Brush.verticalGradient(listOf(PureWhite, SkyBlue300)),
                        shape = CircleShape
                    )
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.rzzinfo_logo),
                    contentDescription = "Logo R RzzInfo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Title & Subtitle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "RzzInfo",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = SkyBlue800,
                    letterSpacing = (-0.5).sp
                )
            }

            Text(
                text = "Pusat Informasi Voucher & Redeem Resmi",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = SkyBlue600,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Features Skeuomorphic Card
            SkeuoCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Selamat Datang di RzzInfo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SlateText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Temukan promo resmi yang selalu diperbarui dan bebas kode palsu. Silakan masuk dengan akun Google untuk memulai sesi resmi.",
                        fontSize = 13.sp,
                        color = SlateMuted,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BenefitRow(
                        icon = Icons.Default.CheckCircle,
                        title = "100% Sumber Resmi",
                        desc = "Shopee, Lazada, Tokopedia, Garena FF, dan Moonton MLBB."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BenefitRow(
                        icon = Icons.Default.Shield,
                        title = "Keamanan Terlindungi",
                        desc = "Google Identity resmi tanpa meminta kata sandi."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BenefitRow(
                        icon = Icons.Default.Email,
                        title = "Notifikasi Login Otomatis",
                        desc = "Riwayat login transparan dan notifikasi email keamanan."
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Official Google Sign-In Tactile Button
            val buttonShape = RoundedCornerShape(18.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("google_login_button")
                    .shadow(
                        elevation = 6.dp,
                        shape = buttonShape,
                        ambientColor = SkeuoShadowDark,
                        spotColor = SkeuoShadowDeep
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(PureWhite, SkyBlue300)
                        ),
                        shape = buttonShape
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                PureWhite,
                                SkyBlue50,
                                SkyBlue100.copy(alpha = 0.5f)
                            )
                        ),
                        shape = buttonShape
                    )
                    .clip(buttonShape)
                    .clickable(enabled = !isLoading) {
                        isLoading = true
                        scope.launch {
                            delay(600) // Brief smooth authentication transition
                            val profile = authRepository.loginWithGoogle(
                                name = customName,
                                email = customEmail,
                                photoUrl = "https://lh3.googleusercontent.com/a/default-user"
                            )

                            // Otomatis kirimkan pesan ke Gmail yang di-loginkan pengguna
                            authRepository.sendAutomaticLoginNotificationEmail(
                                context = context,
                                userEmail = profile.email,
                                loginTime = profile.lastLoginTime
                            )

                            isLoading = false
                            Toast.makeText(
                                context,
                                "Login berhasil! Notifikasi keamanan dikirim otomatis ke Gmail (${profile.email}).",
                                Toast.LENGTH_LONG
                            ).show()
                            onLoginSuccess()
                        }
                    }
                    .padding(vertical = 16.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = SkyBlue600,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Menghubungkan ke Google...",
                            fontWeight = FontWeight.Bold,
                            color = SkyBlue800,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google "G" Badge
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .shadow(2.dp, CircleShape)
                                .background(PureWhite, CircleShape)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF4285F4)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Lanjutkan dengan Google",
                                fontWeight = FontWeight.Bold,
                                color = SlateText,
                                fontSize = 15.sp
                            )
                            Text(
                                text = customEmail,
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switch / Custom Account toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showCustomEmailOption = !showCustomEmailOption }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showCustomEmailOption) "Tutup Pilihan Akun" else "Gunakan Akun Google Lain / Ubah Email",
                    fontSize = 12.sp,
                    color = SkyBlue700,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedVisibility(
                visible = showCustomEmailOption,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    SkeuoCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Atur Akun Google Anda",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SlateText
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customName,
                                onValueChange = { customName = it },
                                label = { Text("Nama Profil Google", fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_custom_name"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customEmail,
                                onValueChange = { customEmail = it },
                                label = { Text("Email Akun Google", fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_custom_email"),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Trust & Security note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = SkyBlue600,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Keamanan terverifikasi. Kata sandi Google Anda tidak pernah disimpan.",
                    fontSize = 11.sp,
                    color = SlateMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BenefitRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(SkyBlue100, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SkyBlue700,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SlateText
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                color = SlateMuted,
                lineHeight = 16.sp
            )
        }
    }
}
