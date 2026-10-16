package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import com.example.data.AuthRepository
import com.example.data.VoucherRepository
import com.example.ui.components.SkeuoBadge
import com.example.ui.components.SkeuoCard
import com.example.ui.theme.*
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateText

enum class AppNavDestination(val label: String, val icon: ImageVector) {
    HOME("Beranda", Icons.Default.Home),
    MARKETPLACE("Marketplace", Icons.Default.ShoppingBag),
    GAME_REDEEM("Redeem", Icons.Default.SportsEsports),
    GUIDES("Panduan", Icons.Default.MenuBook),
    MORE("Menu", Icons.Default.Menu)
}

enum class SubScreen {
    NONE,
    ACCOUNT_SECURITY,
    FAQ,
    TERMS_PRIVACY,
    CONTACT_OWNER,
    ADMIN_DASHBOARD
}

@Composable
fun MainScreen(
    voucherRepository: VoucherRepository,
    authRepository: AuthRepository,
    onLogoutToWelcome: () -> Unit
) {
    var currentTab by remember { mutableStateOf(AppNavDestination.HOME) }
    var subScreen by remember { mutableStateOf(SubScreen.NONE) }

    // Hardware and Gesture Back Handling
    BackHandler(enabled = subScreen != SubScreen.NONE || currentTab != AppNavDestination.HOME) {
        if (subScreen != SubScreen.NONE) {
            subScreen = SubScreen.NONE
        } else if (currentTab != AppNavDestination.HOME) {
            currentTab = AppNavDestination.HOME
        }
    }

    Scaffold(
        bottomBar = {
            SkeuomorphicBottomNavBar(
                currentDestination = currentTab,
                onSelectDestination = {
                    subScreen = SubScreen.NONE
                    currentTab = it
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = if (subScreen != SubScreen.NONE) subScreen else currentTab,
                label = "screen_transition"
            ) { state ->
                when (state) {
                    SubScreen.ACCOUNT_SECURITY -> {
                        AccountSecurityScreen(
                            authRepository = authRepository,
                            onNavigateToAdminDashboard = { subScreen = SubScreen.ADMIN_DASHBOARD },
                            onLogout = onLogoutToWelcome
                        )
                    }
                    SubScreen.ADMIN_DASHBOARD -> {
                        AdminDashboardScreen(
                            voucherRepository = voucherRepository,
                            authRepository = authRepository,
                            onBack = { subScreen = SubScreen.ACCOUNT_SECURITY }
                        )
                    }
                    SubScreen.FAQ -> {
                        FaqScreen()
                    }
                    SubScreen.TERMS_PRIVACY -> {
                        TermsAndPrivacyScreen()
                    }
                    SubScreen.CONTACT_OWNER -> {
                        ContactOwnerScreen()
                    }
                    AppNavDestination.HOME -> {
                        HomeScreen(
                            voucherRepository = voucherRepository,
                            authRepository = authRepository,
                            onNavigateToAccount = { subScreen = SubScreen.ACCOUNT_SECURITY },
                            onNavigateToFaq = { subScreen = SubScreen.FAQ }
                        )
                    }
                    AppNavDestination.MARKETPLACE -> {
                        MarketplaceScreen(voucherRepository = voucherRepository)
                    }
                    AppNavDestination.GAME_REDEEM -> {
                        GameRedeemScreen(voucherRepository = voucherRepository)
                    }
                    AppNavDestination.GUIDES -> {
                        GuideScreen()
                    }
                    AppNavDestination.MORE -> {
                        MoreMenuScreen(
                            authRepository = authRepository,
                            onNavigateTo = { target -> subScreen = target },
                            onLogout = onLogoutToWelcome
                        )
                    }
                    else -> {
                        HomeScreen(
                            voucherRepository = voucherRepository,
                            authRepository = authRepository,
                            onNavigateToAccount = { subScreen = SubScreen.ACCOUNT_SECURITY },
                            onNavigateToFaq = { subScreen = SubScreen.FAQ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoreMenuScreen(
    authRepository: AuthRepository,
    onNavigateTo: (SubScreen) -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by authRepository.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(listOf(SkyBlue100, SkyBlue50, PureWhite))
            )
            .padding(16.dp)
    ) {
        // Top Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = "Menu & Layanan RzzInfo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = SkyBlue900
                )
                Text(
                    text = "Pusat akun, panduan, FAQ, dan kontak owner.",
                    fontSize = 12.sp,
                    color = SlateMuted
                )
            }
        }

        SkeuoCard(modifier = Modifier.fillMaxWidth(), elevation = 4.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                MenuItemRow(
                    icon = Icons.Default.Security,
                    title = "Profil & Keamanan Akun",
                    subtitle = currentUser?.email ?: "Google Account",
                    onClick = { onNavigateTo(SubScreen.ACCOUNT_SECURITY) }
                )
                HorizontalDivider(color = SkyBlue100, modifier = Modifier.padding(vertical = 8.dp))
                MenuItemRow(
                    icon = Icons.Default.HelpOutline,
                    title = "Pertanyaan Umum (FAQ)",
                    subtitle = "12 jawaban seputar voucher dan redeem",
                    onClick = { onNavigateTo(SubScreen.FAQ) }
                )
                HorizontalDivider(color = SkyBlue100, modifier = Modifier.padding(vertical = 8.dp))
                MenuItemRow(
                    icon = Icons.Default.Chat,
                    title = "Hubungi Owner (WhatsApp)",
                    subtitle = "0881022762735 • Bantuan & Laporan",
                    onClick = { onNavigateTo(SubScreen.CONTACT_OWNER) }
                )
                HorizontalDivider(color = SkyBlue100, modifier = Modifier.padding(vertical = 8.dp))
                MenuItemRow(
                    icon = Icons.Default.Gavel,
                    title = "Ketentuan & Kebijakan Privasi",
                    subtitle = "Disclaimer resmi dan aturan layanan",
                    onClick = { onNavigateTo(SubScreen.TERMS_PRIVACY) }
                )

                if (currentUser?.isAdmin == true) {
                    HorizontalDivider(color = SkyBlue100, modifier = Modifier.padding(vertical = 8.dp))
                    MenuItemRow(
                        icon = Icons.Default.Security,
                        title = "Dashboard Admin",
                        subtitle = "Tambah, ubah, dan hapus kode",
                        onClick = { onNavigateTo(SubScreen.ADMIN_DASHBOARD) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SkyBlue100, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SkyBlue700,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SlateText
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = SlateMuted
            )
        }
    }
}

@Composable
private fun SkeuomorphicBottomNavBar(
    currentDestination: AppNavDestination,
    onSelectDestination: (AppNavDestination) -> Unit
) {
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = SkeuoShadowDark,
                spotColor = SkeuoShadowDeep
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(listOf(PureWhite, SkyBlue200)),
                shape = shape
            )
            .background(
                brush = Brush.verticalGradient(listOf(PureWhite, SkyBlue50)),
                shape = shape
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppNavDestination.entries.forEach { dest ->
                val isSelected = currentDestination == dest
                val pillShape = RoundedCornerShape(14.dp)

                Column(
                    modifier = Modifier
                        .testTag("nav_item_${dest.name.lowercase()}")
                        .clip(pillShape)
                        .clickable { onSelectDestination(dest) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(if (isSelected) 2.dp else 0.dp, CircleShape)
                            .background(
                                brush = if (isSelected) {
                                    Brush.verticalGradient(listOf(SkyBlue500, SkyBlue700))
                                } else {
                                    Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                                },
                                shape = CircleShape
                            )
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = dest.icon,
                            contentDescription = dest.label,
                            tint = if (isSelected) PureWhite else SlateMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = dest.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = if (isSelected) SkyBlue800 else SlateMuted
                    )
                }
            }
        }
    }
}
