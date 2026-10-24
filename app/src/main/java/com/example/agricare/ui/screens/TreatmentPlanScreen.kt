package com.example.agricare.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agricare.data.model.TreatmentStepItem
import com.example.agricare.ui.AppScreen
import com.example.agricare.ui.AgriCareViewModel
import com.example.agricare.ui.components.AgriCareCard
import com.example.agricare.ui.components.AgriCareTopBar
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SageBackground
import com.example.ui.theme.SageCardBorder
import com.example.ui.theme.SageContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TreatmentPlanScreen(viewModel: AgriCareViewModel) {
    val strings = viewModel.strings
    val diagnosis by viewModel.activeDiagnosis.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val diag = diagnosis ?: return

    val defaultSteps = listOf(
        TreatmentStepItem("s1", 1, "Remove severely affected leaves", "Cut and destroy infected parts.", "now", true),
        TreatmentStepItem("s2", 2, "Use recommended fungicide", "Carbendazim (0.1%) or Tricyclazole (0.6 mL/L water).", "now", false),
        TreatmentStepItem("s3", 3, "Maintain proper water level", "Avoid water stagnation in the field.", "soon", false),
        TreatmentStepItem("s4", 4, "Ensure good air circulation", "Keep proper spacing between plants.", "soon", false),
        TreatmentStepItem("s5", 5, "Follow crop rotation", "Avoid planting rice in the same field for consecutive seasons.", "monitor", false)
    )

    val steps = if (diag.immediateSteps.isNotEmpty()) diag.immediateSteps else defaultSteps

    val preventionSteps = if (diag.preventionSteps.isNotEmpty()) {
        diag.preventionSteps
    } else {
        listOf(
            "Use certified disease-resistant seeds approved by local research stations",
            "Avoid excessive chemical nitrogen fertilizer applications",
            "Maintain balanced N-P-K nutrition with organic compost",
            "Ensure wide row spacing (minimum 20 cm) for canopy aeration",
            "Deep plow infected stubble immediately after harvest"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SageBackground)
    ) {
        AgriCareTopBar(
            title = strings.treatmentPlanTitle,
            subtitle = diag.cropGuess,
            onBackClick = { viewModel.navigateTo(AppScreen.ANALYSIS_RESULT) }
        )

        // Segmented Tabs: Steps | Prevention
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
                        text = strings.tabSteps,
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
                        text = strings.tabPrevention,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) ForestGreenPrimary else TextSecondary
                        )
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            if (selectedTab == 0) {
                // Steps List
                items(steps) { step ->
                    TreatmentStepCard(
                        step = step,
                        onToggle = { viewModel.toggleTreatmentStep(step.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                // Prevention List
                items(preventionSteps) { prevText ->
                    AgriCareCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SageContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = prevText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Safety Warning Banner (PRD Section 6.10)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SageContainer.copy(alpha = 0.7f))
                        .border(1.dp, ForestGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ForestGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.safetyBanner,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ForestGreenDark,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
fun TreatmentStepCard(
    step: TreatmentStepItem,
    onToggle: () -> Unit
) {
    AgriCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .testTag("step_card_${step.orderIndex}"),
        backgroundColor = if (step.isCompleted) Color(0xFFF4F9F5) else Color.White,
        borderColor = if (step.isCompleted) ForestGreenPrimary.copy(alpha = 0.4f) else SageCardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Numbered Circle Badge or Checked Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (step.isCompleted) ForestGreenPrimary else ForestGreenDark
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (step.isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = step.orderIndex.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        textDecoration = if (step.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                if (step.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}
