package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.AuthRepository
import com.example.data.VoucherRepository
import com.example.ui.screens.MainScreen
import com.example.ui.screens.WelcomeLoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SkyBlue50

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val voucherRepository = remember { VoucherRepository(applicationContext) }
            val authRepository = remember { AuthRepository(applicationContext) }

            val currentUser by authRepository.currentUser.collectAsState()

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SkyBlue50
                ) {
                    if (currentUser == null) {
                        WelcomeLoginScreen(
                            authRepository = authRepository,
                            onLoginSuccess = {
                                // Handled via state observation
                            }
                        )
                    } else {
                        MainScreen(
                            voucherRepository = voucherRepository,
                            authRepository = authRepository,
                            onLogoutToWelcome = {
                                // Handled via auth state observation
                            }
                        )
                    }
                }
            }
        }
    }
}
