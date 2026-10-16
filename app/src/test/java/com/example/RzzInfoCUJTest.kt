package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AuthRepository
import com.example.data.VoucherRepository
import com.example.model.ItemStatus
import com.example.model.VoucherCategory
import com.example.model.VoucherItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RzzInfoCUJTest {

    private lateinit var context: Context
    private lateinit var voucherRepository: VoucherRepository
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        voucherRepository = VoucherRepository(context)
        authRepository = AuthRepository(context)
    }

    @Test
    fun testInitialVouchersLoaded() {
        val list = voucherRepository.vouchers.value
        assertTrue("Should have initial official vouchers", list.isNotEmpty())

        val shopee = list.any { it.category == VoucherCategory.SHOPEE }
        val lazada = list.any { it.category == VoucherCategory.LAZADA }
        val tokopedia = list.any { it.category == VoucherCategory.TOKOPEDIA }
        val ff = list.any { it.category == VoucherCategory.FREE_FIRE }
        val ml = list.any { it.category == VoucherCategory.MOBILE_LEGENDS }

        assertTrue("Contains Shopee", shopee)
        assertTrue("Contains Lazada", lazada)
        assertTrue("Contains Tokopedia", tokopedia)
        assertTrue("Contains Free Fire", ff)
        assertTrue("Contains Mobile Legends", ml)
    }

    @Test
    fun testAddAndUpdateVoucher() {
        val newItem = VoucherItem(
            id = "test-cuj-01",
            category = VoucherCategory.FREE_FIRE,
            title = "Test Booyah Event",
            code = "BOOYAH2026",
            benefit = "1x Room Card",
            minSpend = "-",
            maxDiscount = "-",
            validUntil = "31 Des 2026",
            status = ItemStatus.ACTIVE,
            source = "Official Garena",
            terms = listOf("S&K berlaku"),
            claimUrl = "https://reward.ff.garena.com/id",
            dateAdded = "16 Okt 2026"
        )
        voucherRepository.addVoucher(newItem)

        val found = voucherRepository.vouchers.value.firstOrNull { it.id == "test-cuj-01" }
        assertNotNull(found)
        assertEquals("BOOYAH2026", found?.code)

        // Update to EXPIRED
        val updated = newItem.copy(status = ItemStatus.EXPIRED)
        voucherRepository.updateVoucher(updated)

        val updatedFound = voucherRepository.vouchers.value.firstOrNull { it.id == "test-cuj-01" }
        assertEquals(ItemStatus.EXPIRED, updatedFound?.status)

        // Delete
        voucherRepository.deleteVoucher("test-cuj-01")
        val afterDelete = voucherRepository.vouchers.value.firstOrNull { it.id == "test-cuj-01" }
        assertTrue(afterDelete == null)
    }

    @Test
    fun testGoogleAuthSessionAndEmailIntent() {
        val userEmail = "raskaaidilputraputra@gmail.com"
        val user = authRepository.loginWithGoogle(
            name = "Raska Aidil Putra",
            email = userEmail
        )

        assertNotNull(user)
        assertEquals(userEmail, user.email)
        assertTrue("Owner email is granted admin access", user.isAdmin)

        // Check history
        val history = authRepository.loginHistory.value
        assertTrue("Login history is recorded", history.isNotEmpty())
        assertEquals("Berhasil", history.first().status)

        // Check email intent formatting
        val intent = authRepository.composeSecurityNotificationEmailIntent(user.email, user.lastLoginTime)
        assertEquals("Anda telah login di RzzInfo", intent.getStringExtra(android.content.Intent.EXTRA_SUBJECT))
        val body = intent.getStringExtra(android.content.Intent.EXTRA_TEXT)
        assertTrue(body?.contains("Halo, akun Google Anda baru saja digunakan") == true)
        assertTrue(body?.contains("Tim RzzInfo") == true)

        // Test automatic email dispatch
        authRepository.sendAutomaticLoginNotificationEmail(context, user.email, user.lastLoginTime)
        val emailStatus = authRepository.lastSentEmailStatus.value
        assertNotNull(emailStatus)
        assertTrue(emailStatus?.contains(userEmail) == true)

        // Test Logout
        authRepository.logout()
        assertTrue(authRepository.currentUser.value == null)
    }

    @Test
    fun testMidnightRefreshAndLeaveUntouchedIfNoNewCodes() {
        val countBefore = voucherRepository.vouchers.value.size

        // Force check 00:00 WIB refresh
        val result = voucherRepository.checkAndApplyDailyMidnightRefresh(forceCheck = true)
        val countAfter = voucherRepository.vouchers.value.size

        // Either new drops were added or count stayed exactly the same ("biarkan saja")
        assertTrue(countAfter >= countBefore)
        if (result.newlyAddedCount == 0) {
            assertEquals("When no new codes available, list is left as-is", countBefore, countAfter)
            assertTrue(result.message.contains("Tidak ada rilis kode baru") || result.message.contains("dipertahankan"))
        } else {
            assertTrue(result.newlyAddedCount > 0)
            assertTrue(result.message.contains("berhasil dimuat"))
        }

        // Running a second time immediately should find no new drops and leave list untouched
        val secondResult = voucherRepository.checkAndApplyDailyMidnightRefresh(forceCheck = false)
        val countSecond = voucherRepository.vouchers.value.size
        assertEquals(countAfter, countSecond)
        assertEquals(0, secondResult.newlyAddedCount)
    }
}
