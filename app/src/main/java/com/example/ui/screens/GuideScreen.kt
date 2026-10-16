package com.example.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FaqAndGuidesData
import com.example.ui.components.SkeuoCard
import com.example.ui.theme.*

enum class GuideType {
    VOUCHER_MARKETPLACE,
    FREE_FIRE,
    MOBILE_LEGENDS
}

@Composable
fun GuideScreen() {
    var selectedGuide by remember { mutableStateOf(GuideType.VOUCHER_MARKETPLACE) }

    val currentSteps = when (selectedGuide) {
        GuideType.VOUCHER_MARKETPLACE -> FaqAndGuidesData.voucherGuideSteps
        GuideType.FREE_FIRE -> FaqAndGuidesData.ffGuideSteps
        GuideType.MOBILE_LEGENDS -> FaqAndGuidesData.mlbbGuideSteps
    }

    val activeColor = when (selectedGuide) {
        GuideType.VOUCHER_MARKETPLACE -> ShopeeBrand
        GuideType.FREE_FIRE -> FreeFireBrand
        GuideType.MOBILE_LEGENDS -> MobileLegendsBrand
    }

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
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = SkyBlue600,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Panduan Resmi Penggunaan",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = SkyBlue900
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Petunjuk langkah demi langkah penukaran voucher & kode redeem.",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                }
            }
        }

        // Guide Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GuideTabPill(
                    label = "Voucher Belanja",
                    isSelected = selectedGuide == GuideType.VOUCHER_MARKETPLACE,
                    onClick = { selectedGuide = GuideType.VOUCHER_MARKETPLACE },
                    modifier = Modifier.weight(1f)
                )
                GuideTabPill(
                    label = "Free Fire",
                    isSelected = selectedGuide == GuideType.FREE_FIRE,
                    onClick = { selectedGuide = GuideType.FREE_FIRE },
                    modifier = Modifier.weight(0.9f)
                )
                GuideTabPill(
                    label = "Mobile Legends",
                    isSelected = selectedGuide == GuideType.MOBILE_LEGENDS,
                    onClick = { selectedGuide = GuideType.MOBILE_LEGENDS },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Summary Card
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = activeColor.copy(alpha = 0.4f),
                    elevation = 3.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = when (selectedGuide) {
                                GuideType.VOUCHER_MARKETPLACE -> "Panduan Klaim & Pakai Voucher Marketplace"
                                GuideType.FREE_FIRE -> "Panduan Klaim Kode Redeem Garena Free Fire"
                                GuideType.MOBILE_LEGENDS -> "Panduan Penukaran Kode Moonton Mobile Legends"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = activeColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (selectedGuide) {
                                GuideType.VOUCHER_MARKETPLACE -> "Pastikan voucher diklaim sebelum checkout dan memenuhi batasan toko atau kategori produk."
                                GuideType.FREE_FIRE -> "Kode resmi terdiri dari 12 karakter. Akun guest tidak bisa menukar hadiah; tautkan akun terlebih dahulu."
                                GuideType.MOBILE_LEGENDS -> "Diperlukan ID & Server ID akun in-game. Kode verifikasi dikirimkan ke kotak pesan in-game."
                            },
                            fontSize = 12.sp,
                            color = SlateText,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Step by Step Items
        itemsIndexed(currentSteps) { index, stepText ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Step Number Badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .shadow(2.dp, CircleShape)
                                .background(
                                    brush = Brush.verticalGradient(listOf(SkyBlue500, SkyBlue700)),
                                    shape = CircleShape
                                )
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PureWhite
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = stepText,
                            fontSize = 13.sp,
                            color = SlateText,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideTabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .shadow(if (isSelected) 3.dp else 1.dp, shape)
            .border(
                1.dp,
                if (isSelected) SkyBlue600 else SkyBlue200,
                shape
            )
            .background(
                if (isSelected) SkyBlue600 else PureWhite,
                shape
            )
            .clip(shape)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) PureWhite else SlateText
        )
    }
}
