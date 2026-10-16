package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SkeuoButton
import com.example.ui.components.SkeuoCard
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue100
import com.example.ui.theme.SkyBlue200
import com.example.ui.theme.SkyBlue50
import com.example.ui.theme.SkyBlue600
import com.example.ui.theme.SkyBlue700
import com.example.ui.theme.SkyBlue900
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateText

@Composable
fun ContactOwnerScreen() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val whatsappNumber = "+62881022762735"
    val whatsappUrl = "https://wa.me/62881022762735?text=Halo%20Owner%20RzzInfo%2C%20saya%20ingin%20melaporkan%2Fmenanyakan%20perihal%20voucher%20atau%20kode%20redeem%3A"

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
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = null,
                            tint = SkyBlue600,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hubungi Owner RzzInfo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = SkyBlue900
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Layanan bantuan, pelaporan kode kedaluwarsa, atau pertanyaan seputar voucher.",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                }
            }
        }

        // Main Contact Card
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // WhatsApp Icon Badge
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .shadow(6.dp, CircleShape)
                                .background(Color(0xFF25D366), CircleShape)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "WhatsApp",
                                tint = PureWhite,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "WhatsApp Resmi Owner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = SlateText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = whatsappNumber,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFF1E8E3E)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Required Instruction Note
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SkyBlue200, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                .background(SkyBlue50, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Jika menemukan kode yang salah, voucher kedaluwarsa, atau mengalami masalah saat menggunakan website/aplikasi, silakan hubungi owner.",
                                fontSize = 13.sp,
                                color = SlateText,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Direct WhatsApp Button
                        SkeuoButton(
                            text = "Buka WhatsApp Sekarang",
                            onClick = {
                                openWhatsApp(context, whatsappUrl)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_contact_whatsapp"),
                            icon = Icons.Default.Chat,
                            isPrimary = true,
                            backgroundColor = Color(0xFF25D366)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Copy Number Button
                        SkeuoButton(
                            text = "Salin Nomor WhatsApp",
                            onClick = {
                                clipboardManager.setText(AnnotatedString("0881022762735"))
                                Toast.makeText(context, "Nomor 0881022762735 disalin!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.ContentCopy,
                            isPrimary = false
                        )
                    }
                }
            }
        }

        // Additional Contact Details
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 3.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SkyBlue600,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Jam Operasional & Dukungan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SlateText
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Pesan WhatsApp dibalas setiap hari (08:00 - 22:00 WIB).\n• Laporan kode yang tidak valid akan segera diperbarui dalam daftar voucher setelah diverifikasi tim kurasi RzzInfo.",
                            fontSize = 12.sp,
                            color = SlateMuted,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

private fun openWhatsApp(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Aplikasi WhatsApp tidak ditemukan. Membuka di browser...", Toast.LENGTH_SHORT).show()
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (err: Exception) {
            Toast.makeText(context, "Gagal membuka tautan WhatsApp.", Toast.LENGTH_SHORT).show()
        }
    }
}
