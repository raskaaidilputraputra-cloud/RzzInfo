package com.example.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ItemStatus
import com.example.model.VoucherCategory
import com.example.model.VoucherItem
import com.example.ui.theme.FreeFireBrand
import com.example.ui.theme.LazadaBrand
import com.example.ui.theme.MobileLegendsBrand
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopeeBrand
import com.example.ui.theme.SkyBlue100
import com.example.ui.theme.SkyBlue200
import com.example.ui.theme.SkyBlue600
import com.example.ui.theme.SkyBlue700
import com.example.ui.theme.SkyBlue800
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateText
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusExpired
import com.example.ui.theme.StatusExpiring
import com.example.ui.theme.TokopediaBrand

@Composable
fun VoucherCard(
    item: VoucherItem,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var showTermsDialog by remember { mutableStateOf(false) }

    val brandColor = when (item.category) {
        VoucherCategory.SHOPEE -> ShopeeBrand
        VoucherCategory.LAZADA -> LazadaBrand
        VoucherCategory.TOKOPEDIA -> TokopediaBrand
        VoucherCategory.FREE_FIRE -> FreeFireBrand
        VoucherCategory.MOBILE_LEGENDS -> MobileLegendsBrand
    }

    val statusColor = when (item.status) {
        ItemStatus.ACTIVE -> StatusActive
        ItemStatus.EXPIRING_SOON -> StatusExpiring
        ItemStatus.EXPIRED -> StatusExpired
    }

    SkeuoCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voucher_card_${item.id}"),
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Category Tag & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(brandColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.category.displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = brandColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    SkeuoBadge(
                        text = item.badgeTag,
                        badgeColor = SkyBlue600,
                        textColor = SkyBlue800
                    )
                }

                SkeuoBadge(
                    text = item.status.label,
                    badgeColor = statusColor,
                    textColor = statusColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SlateText,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Benefit Highlight Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = brandColor.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = brandColor.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = item.benefit,
                    fontWeight = FontWeight.ExtraBold,
                    color = brandColor,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Coupon Code Slot (Skeuomorphic Ticket Cutout)
            CouponSlot(
                code = item.code,
                onCopied = { code ->
                    Toast.makeText(
                        context,
                        "Kode $code berhasil disalin ke papan klip!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata: Minimum Spend, Max Discount, Valid Until
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (item.minSpend != "-") {
                    Column {
                        Text(text = "Min. Belanja", fontSize = 11.sp, color = SlateMuted)
                        Text(
                            text = item.minSpend,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateText
                        )
                    }
                }

                if (item.maxDiscount != "-") {
                    Column {
                        Text(text = "Maks. Diskon", fontSize = 11.sp, color = SlateMuted)
                        Text(
                            text = item.maxDiscount,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateText
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = SlateMuted,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Masa Berlaku", fontSize = 11.sp, color = SlateMuted)
                    }
                    Text(
                        text = item.validUntil,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.status == ItemStatus.EXPIRED) StatusExpired else SkyBlue800
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SkyBlue100)
            Spacer(modifier = Modifier.height(10.dp))

            // Source & Verification Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Terverifikasi",
                    tint = SkyBlue600,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sumber: ${item.source}",
                    fontSize = 11.sp,
                    color = SlateMuted,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Syarat & Ketentuan Button
                SkeuoButton(
                    text = "Syarat & S&K",
                    onClick = { showTermsDialog = true },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_terms_${item.id}",
                    icon = Icons.Default.Info,
                    isPrimary = false
                )

                // Claim Official Button
                val claimLabel = when (item.category) {
                    VoucherCategory.SHOPEE -> "Buka Shopee"
                    VoucherCategory.LAZADA -> "Buka Lazada"
                    VoucherCategory.TOKOPEDIA -> "Buka Tokopedia"
                    VoucherCategory.FREE_FIRE -> "Klaim Garena"
                    VoucherCategory.MOBILE_LEGENDS -> "Klaim MLBB"
                }

                SkeuoButton(
                    text = claimLabel,
                    onClick = {
                        openOfficialUrl(context, item.claimUrl)
                    },
                    modifier = Modifier.weight(1.2f),
                    testTag = "btn_claim_${item.id}",
                    icon = Icons.Default.OpenInNew,
                    isPrimary = true,
                    backgroundColor = brandColor
                )
            }

            // Optional Admin Controls
            if (onEdit != null || onDelete != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (onEdit != null) {
                        Text(
                            text = "Edit Kode",
                            color = SkyBlue700,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onEdit() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (onDelete != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hapus",
                            color = StatusExpired,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onDelete() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    text = "Syarat & Ketentuan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = SlateText
                )
            },
            text = {
                Column {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = brandColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    item.terms.forEachIndexed { index, term ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• ",
                                fontWeight = FontWeight.Bold,
                                color = SkyBlue600
                            )
                            Text(
                                text = term,
                                fontSize = 13.sp,
                                color = SlateText,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Tutup", fontWeight = FontWeight.Bold, color = SkyBlue700)
                }
            }
        )
    }
}

private fun openOfficialUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Tidak dapat membuka tautan resmi: $url", Toast.LENGTH_LONG).show()
    }
}
