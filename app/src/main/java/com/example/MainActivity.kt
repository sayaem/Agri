package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.agricare.ui.AppScreen
import com.example.agricare.ui.AgriCareViewModel
import com.example.agricare.ui.MainTab
import com.example.agricare.ui.components.AgriCareBottomBar
import com.example.agricare.ui.screens.AboutPrivacyScreen
import com.example.agricare.ui.screens.AnalysisLoadingScreen
import com.example.agricare.ui.screens.AnalysisResultScreen
import com.example.agricare.ui.screens.CalendarScreen
import com.example.agricare.ui.screens.CropDetailScreen
import com.example.agricare.ui.screens.DiagnoseCameraScreen
import com.example.agricare.ui.screens.HomeScreen
import com.example.agricare.ui.screens.MyCropsScreen
import com.example.agricare.ui.screens.NotificationsScreen
import com.example.agricare.ui.screens.OnboardingScreen
import com.example.agricare.ui.screens.ProfileScreen
import com.example.agricare.ui.screens.SettingsScreen
import com.example.agricare.ui.screens.SplashScreen
import com.example.agricare.ui.screens.TreatmentPlanScreen
import com.example.agricare.ui.screens.VoiceAssistantScreen
import com.example.ui.theme.AgriCareTheme
import com.example.ui.theme.SageBackground

class MainActivity : ComponentActivity() {
    private val viewModel: AgriCareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                "dark" -> true
                else -> false // Light is default per PRD Section 6.17
            }

            AgriCareTheme(darkTheme = isDark) {
                AgriCareAppShell(viewModel = viewModel, onFinish = { finish() })
            }
        }
    }
}

@Composable
fun AgriCareAppShell(
    viewModel: AgriCareViewModel,
    onFinish: () -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()

    // Handle system back navigation gracefully
    BackHandler {
        when (currentScreen) {
            AppScreen.SPLASH -> onFinish()
            AppScreen.ONBOARDING -> onFinish()
            AppScreen.MAIN_HOME -> {
                if (currentTab != MainTab.HOME) {
                    viewModel.selectTab(MainTab.HOME)
                } else {
                    onFinish()
                }
            }
            AppScreen.DIAGNOSE_CAMERA -> viewModel.navigateTo(AppScreen.MAIN_HOME)
            AppScreen.ANALYSIS_LOADING -> viewModel.navigateTo(AppScreen.MAIN_HOME)
            AppScreen.ANALYSIS_RESULT -> viewModel.navigateTo(AppScreen.MAIN_HOME)
            AppScreen.TREATMENT_PLAN -> viewModel.navigateTo(AppScreen.ANALYSIS_RESULT)
            AppScreen.CROP_DETAIL -> viewModel.navigateTo(AppScreen.MAIN_HOME)
            AppScreen.NOTIFICATIONS -> viewModel.navigateTo(AppScreen.MAIN_HOME)
            AppScreen.VOICE_ASSISTANT -> viewModel.navigateTo(AppScreen.MAIN_HOME)
            AppScreen.SETTINGS -> viewModel.selectTab(MainTab.PROFILE)
            AppScreen.ABOUT_PRIVACY -> viewModel.selectTab(MainTab.PROFILE)
            else -> viewModel.navigateTo(AppScreen.MAIN_HOME)
        }
    }

    val showBottomBar = currentScreen == AppScreen.MAIN_HOME

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AgriCareBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            }
        },
        containerColor = SageBackground,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp.Hairline)
        ) {
            when (currentScreen) {
                AppScreen.SPLASH -> SplashScreen(viewModel = viewModel)
                AppScreen.ONBOARDING -> OnboardingScreen(viewModel = viewModel)
                AppScreen.MAIN_HOME -> {
                    when (currentTab) {
                        MainTab.HOME -> HomeScreen(viewModel = viewModel)
                        MainTab.MY_CROPS -> MyCropsScreen(viewModel = viewModel)
                        MainTab.CALENDAR -> CalendarScreen(viewModel = viewModel)
                        MainTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                        MainTab.DIAGNOSE -> DiagnoseCameraScreen(viewModel = viewModel)
                    }
                }
                AppScreen.DIAGNOSE_CAMERA -> DiagnoseCameraScreen(viewModel = viewModel)
                AppScreen.ANALYSIS_LOADING -> AnalysisLoadingScreen(viewModel = viewModel)
                AppScreen.ANALYSIS_RESULT -> AnalysisResultScreen(viewModel = viewModel)
                AppScreen.TREATMENT_PLAN -> TreatmentPlanScreen(viewModel = viewModel)
                AppScreen.CROP_DETAIL -> CropDetailScreen(viewModel = viewModel)
                AppScreen.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
                AppScreen.VOICE_ASSISTANT -> VoiceAssistantScreen(viewModel = viewModel)
                AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                AppScreen.ABOUT_PRIVACY -> AboutPrivacyScreen(viewModel = viewModel)
                else -> HomeScreen(viewModel = viewModel)
            }
        }
    }
}
