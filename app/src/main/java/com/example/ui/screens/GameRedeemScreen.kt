package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VoucherRepository
import com.example.model.VoucherCategory
import com.example.ui.components.SkeuoButton
import com.example.ui.components.SkeuoCard
import com.example.ui.components.VoucherCard
import com.example.ui.theme.FreeFireBrand
import com.example.ui.theme.MobileLegendsBrand
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue100
import com.example.ui.theme.SkyBlue200
import com.example.ui.theme.SkyBlue50
import com.example.ui.theme.SkyBlue900
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateText

@Composable
fun GameRedeemScreen(
    voucherRepository: VoucherRepository
) {
    val context = LocalContext.current
    val vouchers by voucherRepository.vouchers.collectAsState()
    var selectedGame by remember { mutableStateOf(VoucherCategory.FREE_FIRE) }

    val gameCodes = remember(vouchers, selectedGame) {
        vouchers.filter { it.category == selectedGame }
    }

    val brandColor = if (selectedGame == VoucherCategory.FREE_FIRE) FreeFireBrand else MobileLegendsBrand

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
        // Top Header
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
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = brandColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kode Redeem Game Resmi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = SkyBlue900
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kode resmi Garena Free Fire dan Moonton Mobile Legends. Bebas kode palsu.",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                }
            }
        }

        // Game Selector Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GameTabItem(
                    title = "Free Fire (Garena)",
                    color = FreeFireBrand,
                    isSelected = selectedGame == VoucherCategory.FREE_FIRE,
                    onClick = { selectedGame = VoucherCategory.FREE_FIRE },
                    modifier = Modifier.weight(1f)
                )
                GameTabItem(
                    title = "Mobile Legends (MLBB)",
                    color = MobileLegendsBrand,
                    isSelected = selectedGame == VoucherCategory.MOBILE_LEGENDS,
                    onClick = { selectedGame = VoucherCategory.MOBILE_LEGENDS },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Official Portal Direct Button Banner
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = brandColor.copy(alpha = 0.5f),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedGame == VoucherCategory.FREE_FIRE) "Portal Resmi Garena Rewards" else "Portal Resmi Moonton Code Exchange",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = brandColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (selectedGame == VoucherCategory.FREE_FIRE) "reward.ff.garena.com/id" else "m.mobilelegends.com/en/codexchange",
                                    fontSize = 12.sp,
                                    color = SlateMuted
                                )
                            }

                            val portalUrl = if (selectedGame == VoucherCategory.FREE_FIRE) {
                                "https://reward.ff.garena.com/id"
                            } else {
                                "https://m.mobilelegends.com/en/codexchange"
                            }

                            SkeuoButton(
                                text = "Buka Portal",
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portalUrl)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Gagal membuka web: $portalUrl", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                isPrimary = true,
                                backgroundColor = brandColor,
                                icon = Icons.Default.OpenInNew
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (selectedGame == VoucherCategory.FREE_FIRE) {
                                "Catatan: Akun guest tidak dapat menukarkan hadiah. Hubungkan akun ke Facebook, Google, atau VK."
                            } else {
                                "Catatan: Diperlukan ID Akun & Server ID. Kode verifikasi akan dikirim ke mailbox in-game MLBB Anda."
                            },
                            fontSize = 11.sp,
                            color = SlateText,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // List of Codes
        if (gameCodes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada kode aktif baru untuk ${selectedGame.displayName}.",
                        color = SlateMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(gameCodes, key = { it.id }) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    VoucherCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun GameTabItem(
    title: String,
    color: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .shadow(if (isSelected) 4.dp else 1.dp, shape)
            .border(
                1.2.dp,
                if (isSelected) color else SkyBlue200,
                shape
            )
            .background(
                if (isSelected) color else PureWhite,
                shape
            )
            .clip(shape)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) PureWhite else SlateText
        )
    }
}
