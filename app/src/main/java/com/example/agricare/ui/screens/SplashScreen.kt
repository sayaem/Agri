package com.example.agricare.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.agricare.ui.AppScreen
import com.example.agricare.ui.AgriCareViewModel
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: AgriCareViewModel) {
    val isOnboardingDone = viewModel.isOnboardingCompleted.value
    val strings = viewModel.strings

    LaunchedEffect(Unit) {
        delay(1600)
        if (isOnboardingDone) {
            viewModel.navigateTo(AppScreen.MAIN_HOME)
        } else {
            viewModel.navigateTo(AppScreen.ONBOARDING)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForestGreenDark),
        contentAlignment = Alignment.Center
    ) {
        // Botanical background image
        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.35f
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(ForestGreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "AgriCare Logo",
                    modifier = Modifier.size(72.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = strings.appName,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = strings.tagline,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFD4EAD9),
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Powered by AI • Built for Farmers",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            )
        }
    }
}
