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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthRepository
import com.example.data.VoucherRepository
import com.example.model.ItemStatus
import com.example.model.VoucherCategory
import com.example.model.VoucherItem
import com.example.ui.components.SkeuoBadge
import com.example.ui.components.SkeuoButton
import com.example.ui.components.SkeuoCard
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue100
import com.example.ui.theme.SkyBlue200
import com.example.ui.theme.SkyBlue50
import com.example.ui.theme.SkyBlue600
import com.example.ui.theme.SkyBlue700
import com.example.ui.theme.SkyBlue800
import com.example.ui.theme.SkyBlue900
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateText
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusExpired
import com.example.ui.theme.StatusExpiring
import java.util.UUID

@Composable
fun AdminDashboardScreen(
    voucherRepository: VoucherRepository,
    authRepository: AuthRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by authRepository.currentUser.collectAsState()
    val vouchers by voucherRepository.vouchers.collectAsState()

    // Strict Authorization Check: Only true admins can access
    val isAdmin = currentUser?.isAdmin == true

    var showFormDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<VoucherItem?>(null) }
    var itemToDelete by remember { mutableStateOf<VoucherItem?>(null) }

    if (!isAdmin) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SkyBlue50)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            SkeuoCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = StatusExpired,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Akses Ditolak (403)",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = StatusExpired
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Akun ini (${currentUser?.email ?: "Belum login"}) tidak memiliki hak akses administrator. Dashboard admin terlindungi.",
                        fontSize = 13.sp,
                        color = SlateMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    SkeuoButton(
                        text = "Kembali ke Beranda",
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = true
                    )
                }
            }
        }
        return
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
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = SkyBlue900
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dashboard Administrator",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SkyBlue900
                        )
                        Text(
                            text = "Kelola voucher, kode redeem, status, dan data resmi.",
                            fontSize = 11.sp,
                            color = SlateMuted
                        )
                    }
                }
            }
        }

        // Stats Overview
        item {
            val total = vouchers.size
            val active = vouchers.count { it.status == ItemStatus.ACTIVE }
            val expiring = vouchers.count { it.status == ItemStatus.EXPIRING_SOON }
            val expired = vouchers.count { it.status == ItemStatus.EXPIRED }

            Box(modifier = Modifier.padding(16.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Statistik Voucher & Kode Redeem",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SlateText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatCardItem(label = "Total", value = "$total", color = SkyBlue700)
                            StatCardItem(label = "Aktif", value = "$active", color = StatusActive)
                            StatCardItem(label = "Segera", value = "$expiring", color = StatusExpiring)
                            StatCardItem(label = "Kedaluwarsa", value = "$expired", color = StatusExpired)
                        }
                    }
                }
            }
        }

        // Action Toolbar: Add New Voucher & Reset
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkeuoButton(
                    text = "Tambah Kode Baru",
                    onClick = {
                        editingItem = null
                        showFormDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_admin_add_new",
                    icon = Icons.Default.Add,
                    isPrimary = true
                )

                SkeuoButton(
                    text = "Reset Data Resmi",
                    onClick = {
                        voucherRepository.resetToDefaults()
                        Toast.makeText(context, "Data berhasil direset ke standar resmi.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Refresh,
                    isPrimary = false
                )
            }
        }

        // Section Title
        item {
            Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
                Text(
                    text = "Daftar Kelola Kode (${vouchers.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = SkyBlue900
                )
            }
        }

        // Manageable Item List
        items(vouchers, key = { it.id }) { item ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                SkeuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.category.displayName,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = SkyBlue800
                            )
                            SkeuoBadge(
                                text = item.status.label,
                                badgeColor = when (item.status) {
                                    ItemStatus.ACTIVE -> StatusActive
                                    ItemStatus.EXPIRING_SOON -> StatusExpiring
                                    ItemStatus.EXPIRED -> StatusExpired
                                },
                                textColor = when (item.status) {
                                    ItemStatus.ACTIVE -> StatusActive
                                    ItemStatus.EXPIRING_SOON -> StatusExpiring
                                    ItemStatus.EXPIRED -> StatusExpired
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SlateText
                        )
                        Text(
                            text = "Kode: ${item.code.ifBlank { "(Klaim via URL)" }} • ${item.benefit}",
                            fontSize = 12.sp,
                            color = SlateMuted
                        )
                        Text(
                            text = "Berlaku: ${item.validUntil} • Sumber: ${item.source}",
                            fontSize = 11.sp,
                            color = SkyBlue700
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Admin Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = if (item.status == ItemStatus.ACTIVE) "Set Kedaluwarsa" else "Set Aktif",
                                color = if (item.status == ItemStatus.ACTIVE) StatusExpired else StatusActive,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        val newStatus = if (item.status == ItemStatus.ACTIVE) ItemStatus.EXPIRED else ItemStatus.ACTIVE
                                        voucherRepository.updateVoucher(item.copy(status = newStatus))
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Edit",
                                color = SkyBlue700,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        editingItem = item
                                        showFormDialog = true
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hapus",
                                color = StatusExpired,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { itemToDelete = item }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showFormDialog) {
        VoucherFormDialog(
            initial = editingItem,
            onDismiss = { showFormDialog = false },
            onSave = { savedItem ->
                if (editingItem != null) {
                    voucherRepository.updateVoucher(savedItem)
                    Toast.makeText(context, "Kode berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                } else {
                    voucherRepository.addVoucher(savedItem)
                    Toast.makeText(context, "Kode baru berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                }
                showFormDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (itemToDelete != null) {
        val target = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Kode Voucher?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus '${target.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        voucherRepository.deleteVoucher(target.id)
                        itemToDelete = null
                        Toast.makeText(context, "Kode berhasil dihapus.", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Hapus", color = StatusExpired, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal", color = SlateMuted)
                }
            }
        )
    }
}

@Composable
private fun StatCardItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = SlateMuted
        )
    }
}

@Composable
private fun VoucherFormDialog(
    initial: VoucherItem?,
    onDismiss: () -> Unit,
    onSave: (VoucherItem) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var benefit by remember { mutableStateOf(initial?.benefit ?: "") }
    var minSpend by remember { mutableStateOf(initial?.minSpend ?: "Rp 0") }
    var maxDiscount by remember { mutableStateOf(initial?.maxDiscount ?: "-") }
    var validUntil by remember { mutableStateOf(initial?.validUntil ?: "31 Des 2026") }
    var source by remember { mutableStateOf(initial?.source ?: "Pusat Promosi Resmi") }
    var claimUrl by remember { mutableStateOf(initial?.claimUrl ?: "https://") }
    var termsText by remember { mutableStateOf(initial?.terms?.joinToString("\n") ?: "Berlaku untuk akun terverifikasi.\nSyarat & ketentuan resmi berlaku.") }
    var selectedCategory by remember { mutableStateOf(initial?.category ?: VoucherCategory.SHOPEE) }
    var selectedStatus by remember { mutableStateOf(initial?.status ?: ItemStatus.ACTIVE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initial != null) "Edit Voucher / Redeem" else "Tambah Voucher Resmi Baru",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Promosi / Voucher") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Kode (Kosongkan jika hanya tautan)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = benefit,
                    onValueChange = { benefit = it },
                    label = { Text("Hadiah / Nilai Diskon") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = validUntil,
                    onValueChange = { validUntil = it },
                    label = { Text("Masa Berlaku") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("Sumber Verifikasi Resmi") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = claimUrl,
                    onValueChange = { claimUrl = it },
                    label = { Text("Tautan Resmi Klaim") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = termsText,
                    onValueChange = { termsText = it },
                    label = { Text("Syarat & Ketentuan (Pisahkan baris)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isBlank() || benefit.isBlank()) return@TextButton
                    val terms = termsText.lines().map { it.trim() }.filter { it.isNotBlank() }
                    val item = VoucherItem(
                        id = initial?.id ?: UUID.randomUUID().toString(),
                        category = selectedCategory,
                        title = title.trim(),
                        code = code.trim().uppercase(),
                        benefit = benefit.trim(),
                        minSpend = minSpend.trim(),
                        maxDiscount = maxDiscount.trim(),
                        validUntil = validUntil.trim(),
                        status = selectedStatus,
                        source = source.trim(),
                        terms = terms,
                        claimUrl = claimUrl.trim(),
                        dateAdded = initial?.dateAdded ?: VoucherRepository.getCurrentJakartaDate(),
                        isVerified = true,
                        badgeTag = "Resmi"
                    )
                    onSave(item)
                }
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold, color = SkyBlue700)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = SlateMuted)
            }
        }
    )
}
