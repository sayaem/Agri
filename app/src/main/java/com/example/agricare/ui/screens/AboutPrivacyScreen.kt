package com.example.agricare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agricare.ui.AppScreen
import com.example.agricare.ui.AgriCareViewModel
import com.example.agricare.ui.components.AgriCareCard
import com.example.agricare.ui.components.AgriCareTopBar
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SageBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AboutPrivacyScreen(viewModel: AgriCareViewModel) {
    val strings = viewModel.strings
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SageBackground)
    ) {
        AgriCareTopBar(
            title = strings.aboutAgriCare,
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_HOME) }
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = ForestGreenPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ForestGreenPrimary,
                    height = 3.dp
                )
            },
            divider = {},
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) ForestGreenPrimary else TextSecondary
                        )
                    )
                }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "Privacy",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) ForestGreenPrimary else TextSecondary
                        )
                    )
                }
            )

            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        text = "Terms",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 2) ForestGreenPrimary else TextSecondary
                        )
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    item {
                        AgriCareCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Color.White
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "AgriCare Plant Health Assistant",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Version 1.0 (Google AI Studio Edition)",
                                    style = MaterialTheme.typography.labelMedium.copy(color = ForestGreenPrimary)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "AgriCare is an English-first, Bangla-localizable plant and crop health assistant engineered for Bangladeshi smallholder farmers, gardeners, and growers. Using Google Gemini vision capabilities, AgriCare analyzes visible leaf symptoms and returns cautious, structured assessments, practical mechanical/cultural next steps, and guidance on when to seek in-person Upazila agricultural extension support.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        lineHeight = 22.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Collaborating Institutions & Reference Citations:\n• Bangladesh Rice Research Institute (BRRI)\n• Bangladesh Agricultural Research Institute (BARI)\n• Department of Agricultural Extension (DAE), Khamarbari, Dhaka",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
                1 -> {
                    item {
                        AgriCareCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Color.White
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Privacy & Data Processing",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "1. Zero Required Accounts: AgriCare MVP does not require personal sign-in or social login.\n\n" +
                                            "2. Image Transmission: When you tap 'Analyze Crop', the selected plant leaf image is transmitted securely over HTTPS to the Google Gemini Vision API strictly for agricultural visual assessment. No personal identity records are attached to this request.\n\n" +
                                            "3. Location & EXIF: AgriCare does not collect or transmit your precise GPS coordinates, background location, or EXIF metadata.\n\n" +
                                            "4. Local Device Storage: Your saved crops, treatment checklists, and diagnosis history are stored locally on your device via Room SQLite database. You can permanently erase all local records at any time from Settings > Clear Local App Data.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }
                }
                2 -> {
                    item {
                        AgriCareCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Color.White
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Terms of Service & Agronomic Disclaimer",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "1. Informational Guidance Only: Visual assessments provided by AgriCare are AI-assisted educational aids and do not constitute certified laboratory diagnoses or guaranteed disease identification.\n\n" +
                                            "2. User Responsibility: Users must exercise personal judgment and strictly adhere to registered chemical product labels, national pesticide regulations, personal protective equipment (PPE) requirements, and pre-harvest intervals.\n\n" +
                                            "3. Emergency Escalation: For rapidly spreading crop blights, heavy pest outbreaks, or high economic stakes, immediately contact your local Sub-Assistant Agriculture Officer (SAAO) or Upazila Agriculture Officer (UAO).",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
