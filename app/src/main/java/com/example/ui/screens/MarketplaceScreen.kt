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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VoucherRepository
import com.example.model.VoucherCategory
import com.example.ui.components.SkeuoCard
import com.example.ui.components.VoucherCard
import com.example.ui.theme.LazadaBrand
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopeeBrand
import com.example.ui.theme.SkyBlue100
import com.example.ui.theme.SkyBlue200
import com.example.ui.theme.SkyBlue50
import com.example.ui.theme.SkyBlue700
import com.example.ui.theme.SkyBlue900
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateText
import com.example.ui.theme.TokopediaBrand

@Composable
fun MarketplaceScreen(
    voucherRepository: VoucherRepository
) {
    val vouchers by voucherRepository.vouchers.collectAsState()
    var selectedMarketplace by remember { mutableStateOf(VoucherCategory.SHOPEE) }

    val marketplaceList = remember(vouchers, selectedMarketplace) {
        vouchers.filter { it.category == selectedMarketplace }
    }

    val brandColor = when (selectedMarketplace) {
        VoucherCategory.SHOPEE -> ShopeeBrand
        VoucherCategory.LAZADA -> LazadaBrand
        VoucherCategory.TOKOPEDIA -> TokopediaBrand
        else -> ShopeeBrand
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
        // Top Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column {
                    Text(
                        text = "Voucher Marketplace Resmi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = SkyBlue900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kompilasi kupon diskon & gratis ongkir resmi Shopee, Lazada, dan Tokopedia.",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                }
            }
        }

        // Marketplace Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MarketplaceTabItem(
                    name = "Shopee",
                    color = ShopeeBrand,
                    isSelected = selectedMarketplace == VoucherCategory.SHOPEE,
                    onClick = { selectedMarketplace = VoucherCategory.SHOPEE },
                    modifier = Modifier.weight(1f)
                )
                MarketplaceTabItem(
                    name = "Lazada",
                    color = LazadaBrand,
                    isSelected = selectedMarketplace == VoucherCategory.LAZADA,
                    onClick = { selectedMarketplace = VoucherCategory.LAZADA },
                    modifier = Modifier.weight(1f)
                )
                MarketplaceTabItem(
                    name = "Tokopedia",
                    color = TokopediaBrand,
                    isSelected = selectedMarketplace == VoucherCategory.TOKOPEDIA,
                    onClick = { selectedMarketplace = VoucherCategory.TOKOPEDIA },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Info Banner for Selected Marketplace
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = brandColor.copy(alpha = 0.4f),
                    elevation = 3.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(brandColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pusat Promo ${selectedMarketplace.displayName}",
                                fontWeight = FontWeight.Bold,
                                color = brandColor,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (selectedMarketplace) {
                                VoucherCategory.SHOPEE -> "Voucher dapat diklaim langsung atau digunakan saat checkout pada aplikasi Shopee. Pastikan syarat minimum transaksi terpenuhi."
                                VoucherCategory.LAZADA -> "Bonus Dadakan dan voucher mega sale dapat digabung dengan Gratis Ongkir sesuai ketentuan Lazada."
                                VoucherCategory.TOKOPEDIA -> "Kupon cashback GoPay Coins dan Bebas Ongkir berlaku untuk Official Store & merchant terpilih."
                                else -> ""
                            },
                            fontSize = 12.sp,
                            color = SlateText,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Listing
        if (marketplaceList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada voucher aktif untuk ${selectedMarketplace.displayName} saat ini.",
                        color = SlateMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(marketplaceList, key = { it.id }) { voucher ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    VoucherCard(item = voucher)
                }
            }
        }
    }
}

@Composable
private fun MarketplaceTabItem(
    name: String,
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
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) PureWhite else SlateText
        )
    }
}
