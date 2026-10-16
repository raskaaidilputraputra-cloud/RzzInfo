package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.example.model.DailyRefreshInfo
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuthRepository
import com.example.data.VoucherRepository
import com.example.model.ItemStatus
import com.example.model.UserProfile
import com.example.model.VoucherCategory
import com.example.ui.components.SkeuoCard
import com.example.ui.components.VoucherCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    voucherRepository: VoucherRepository,
    authRepository: AuthRepository,
    onNavigateToAccount: () -> Unit,
    onNavigateToFaq: () -> Unit
) {
    val context = LocalContext.current
    val vouchers by voucherRepository.vouchers.collectAsState()
    val currentUser by authRepository.currentUser.collectAsState()
    val dailyRefreshInfo by voucherRepository.dailyRefreshStatus.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<VoucherCategory?>(null) }
    var selectedStatus by remember { mutableStateOf<ItemStatus?>(null) }

    val filteredList = remember(vouchers, searchQuery, selectedCategory, selectedStatus) {
        vouchers.filter { item ->
            val matchesCategory = selectedCategory == null || item.category == selectedCategory
            val matchesStatus = selectedStatus == null || item.status == selectedStatus
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                item.title.lowercase().contains(q) ||
                    item.code.lowercase().contains(q) ||
                    item.benefit.lowercase().contains(q) ||
                    item.category.displayName.lowercase().contains(q) ||
                    item.source.lowercase().contains(q)
            }
            matchesCategory && matchesStatus && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        SkyBlue100,
                        SkyBlue50,
                        PureWhite
                    )
                )
            ),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Skeuomorphic Header
        item {
            HeaderSection(
                user = currentUser,
                onAccountClick = onNavigateToAccount,
                onFaqClick = onNavigateToFaq
            )
        }

        // Search Bar (Skeuomorphic Recessed Well)
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SearchSection(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClear = { searchQuery = "" }
                )
            }
        }

        // Hero Announcement Banner
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                HeroBanner()
            }
        }

        // 00:00 WIB Daily Refresh Banner
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                MidnightRefreshBanner(
                    info = dailyRefreshInfo,
                    onManualRefresh = {
                        val result = voucherRepository.checkAndApplyDailyMidnightRefresh(forceCheck = true)
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Category Filter Chips
        item {
            CategoryFilterBar(
                selected = selectedCategory,
                onSelect = { selectedCategory = it }
            )
        }

        // Status Filter Chips
        item {
            StatusFilterBar(
                selected = selectedStatus,
                onSelect = { selectedStatus = it }
            )
        }

        // Section Title & Result Count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Kode & Voucher Resmi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = SkyBlue900
                )
                Text(
                    text = "${filteredList.size} Tersedia",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkyBlue700
                )
            }
        }

        // Empty State or Items
        if (filteredList.isEmpty()) {
            item {
                EmptyStateCard(searchQuery = searchQuery)
            }
        } else {
            items(filteredList, key = { it.id }) { voucher ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    VoucherCard(item = voucher)
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    user: UserProfile?,
    onAccountClick: () -> Unit,
    onFaqClick: () -> Unit
) {
    val shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape, ambientColor = SkeuoShadowDark, spotColor = SkeuoShadowDeep)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        SkyBlue600,
                        SkyBlue700
                    )
                ),
                shape = shape
            )
            .padding(top = 16.dp, bottom = 18.dp, start = 18.dp, end = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // App Logo
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape)
                        .border(1.5.dp, PureWhite, CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rzzinfo_logo),
                        contentDescription = "Logo R RzzInfo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RzzInfo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = PureWhite
                        )
                    }
                    Text(
                        text = "Pusat Voucher & Redeem Resmi",
                        fontSize = 11.sp,
                        color = SkyBlue100
                    )
                }
            }

            // User Profile Avatar / Status Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onFaqClick,
                    modifier = Modifier
                        .size(38.dp)
                        .background(SkyBlue800.copy(alpha = 0.5f), CircleShape)
                        .testTag("btn_header_faq")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "FAQ",
                        tint = PureWhite,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .shadow(3.dp, CircleShape)
                        .border(1.5.dp, PureWhite, CircleShape)
                        .background(PureWhite, CircleShape)
                        .clip(CircleShape)
                        .clickable { onAccountClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_header_account")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Akun",
                            tint = SkyBlue700,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = user?.name?.split(" ")?.firstOrNull() ?: "Profil",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkyBlue900
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape, ambientColor = SkeuoShadowDark)
            .border(1.2.dp, SkyBlue200, shape)
            .background(PureWhite, shape)
            .clip(shape)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Cari voucher, game, atau kode...",
                    fontSize = 13.sp,
                    color = SlateMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = SkyBlue600
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Hapus",
                            tint = SlateMuted
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input")
        )
    }
}

@Composable
private fun HeroBanner() {
    SkeuoCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            SkyBlue50,
                            SkyBlue100
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📢 Informasi Resmi & Terverifikasi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SkyBlue700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kompilasi voucher belanja Shopee, Lazada, Tokopedia, serta kode redeem aktif Free Fire & MLBB langsung dari penerbit.",
                        fontSize = 12.sp,
                        color = SlateText,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterBar(
    selected: VoucherCategory?,
    onSelect: (VoucherCategory?) -> Unit
) {
    val categories = listOf(
        null to "Semua",
        VoucherCategory.SHOPEE to "Shopee",
        VoucherCategory.LAZADA to "Lazada",
        VoucherCategory.TOKOPEDIA to "Tokopedia",
        VoucherCategory.FREE_FIRE to "Free Fire",
        VoucherCategory.MOBILE_LEGENDS to "MLBB"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (cat, label) ->
            val isSelected = selected == cat
            val shape = RoundedCornerShape(12.dp)
            val chipColor = if (isSelected) SkyBlue600 else PureWhite
            val textColor = if (isSelected) PureWhite else SlateText

            Box(
                modifier = Modifier
                    .shadow(if (isSelected) 3.dp else 1.dp, shape)
                    .border(
                        1.dp,
                        if (isSelected) SkyBlue400 else SkyBlue200,
                        shape
                    )
                    .background(chipColor, shape)
                    .clip(shape)
                    .clickable { onSelect(cat) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun StatusFilterBar(
    selected: ItemStatus?,
    onSelect: (ItemStatus?) -> Unit
) {
    val statuses = listOf(
        null to "Semua Status",
        ItemStatus.ACTIVE to "Aktif",
        ItemStatus.EXPIRING_SOON to "Segera Berakhir",
        ItemStatus.EXPIRED to "Kedaluwarsa"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statuses.forEach { (status, label) ->
            val isSelected = selected == status
            val shape = RoundedCornerShape(10.dp)
            val color = when (status) {
                ItemStatus.ACTIVE -> StatusActive
                ItemStatus.EXPIRING_SOON -> StatusExpiring
                ItemStatus.EXPIRED -> StatusExpired
                null -> SkyBlue700
            }

            Box(
                modifier = Modifier
                    .border(
                        1.dp,
                        if (isSelected) color else SkyBlue200,
                        shape
                    )
                    .background(
                        if (isSelected) color.copy(alpha = 0.15f) else PureWhite,
                        shape
                    )
                    .clip(shape)
                    .clickable { onSelect(status) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (isSelected) color else SlateMuted
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(searchQuery: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        SkeuoCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🔍", fontSize = 42.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Belum Ada Kode Aktif",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SlateText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (searchQuery.isNotBlank()) {
                        "Tidak ditemukan voucher atau kode dengan kata kunci \"$searchQuery\". Coba periksa ejaan atau ubah filter pencarian."
                    } else {
                        "Belum ada kode baru yang dirilis oleh penerbit untuk kategori ini saat ini. Data akan diperbarui begitu kanal resmi merilis event baru."
                    },
                    fontSize = 13.sp,
                    color = SlateMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun MidnightRefreshBanner(
    info: DailyRefreshInfo,
    onManualRefresh: () -> Unit
) {
    SkeuoCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 3.dp,
        borderColor = SkyBlue300
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            PureWhite,
                            SkyBlue50
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(SkyBlue100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = SkyBlue700,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Jadwal Refresh: 00:00 WIB",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SkyBlue900
                        )
                        Text(
                            text = "Pembaruan otomatis tiap pergantian hari",
                            fontSize = 10.sp,
                            color = SlateMuted
                        )
                    }
                }

                // Tactile sync button
                Box(
                    modifier = Modifier
                        .shadow(1.dp, RoundedCornerShape(8.dp))
                        .border(1.dp, SkyBlue300, RoundedCornerShape(8.dp))
                        .background(PureWhite, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onManualRefresh() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sinkronkan",
                            tint = SkyBlue700,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Periksa",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkyBlue700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = info.message,
                fontSize = 11.sp,
                color = SlateText,
                lineHeight = 16.sp
            )
        }
    }
}

