package com.example.agricare.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.agricare.ui.AppScreen
import com.example.agricare.ui.AgriCareViewModel
import com.example.agricare.ui.components.AgriCareCard
import com.example.agricare.ui.components.AgriCarePrimaryButton
import com.example.agricare.ui.components.AgriCareTopBar
import com.example.agricare.ui.components.StatusPill
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningBg
import com.example.ui.theme.EmeraldConfidence
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SageBackground
import com.example.ui.theme.SageCardBorder
import com.example.ui.theme.SageContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalysisResultScreen(viewModel: AgriCareViewModel) {
    val context = LocalContext.current
    val strings = viewModel.strings
    val diagnosis by viewModel.activeDiagnosis.collectAsState()
    var addedToCrops by remember { mutableStateOf(false) }

    val diag = diagnosis ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SageBackground)
    ) {
        AgriCareTopBar(
            title = strings.analysisResultTitle,
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_HOME) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Main Disease Card with Thumbnail and Confidence Pill
            AgriCareCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE8ECE9))
                    ) {
                        if (diag.imageUri != null) {
                            AsyncImage(
                                model = diag.imageUri,
                                contentDescription = diag.issueTitle,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = diag.imageRes ?: R.drawable.rice_blast_leaf),
                                contentDescription = diag.issueTitle,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        StatusPill(
                            text = String.format(strings.confidenceFormat, diag.confidencePercent),
                            textColor = EmeraldConfidence
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = diag.issueTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 20.sp
                            )
                        )
                        if (diag.scientificName.isNotBlank()) {
                            Text(
                                text = "(${diag.scientificName})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary description
            Text(
                text = diag.description.ifBlank { "This looks like ${diag.issueTitle} based on the visible leaf symptoms." },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Key Symptoms Section
            Text(
                text = strings.keySymptoms,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            val symptomsList = if (diag.visibleSymptoms.isNotEmpty()) {
                diag.visibleSymptoms
            } else {
                listOf(
                    "Spindle-shaped lesions on leaves",
                    "Yellowing around the spots",
                    "Grayish center with dark border"
                )
            }

            symptomsList.forEach { symptom ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ForestGreenPrimary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = symptom,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Also Possible / Alternative Causes
            val alternatives = if (diag.alternativeCauses.isNotEmpty()) {
                diag.alternativeCauses.joinToString(", ")
            } else {
                "Nutrient deficiency (less likely)"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEBF5EE))
                    .border(1.dp, SageCardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = ForestGreenDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${strings.alternativeCauses} $alternatives",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ForestGreenDark,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Button: View Treatment Plan
            AgriCarePrimaryButton(
                text = strings.viewTreatmentPlan,
                onClick = { viewModel.navigateTo(AppScreen.TREATMENT_PLAN) },
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                modifier = Modifier.testTag("view_treatment_plan_button")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.VOICE_ASSISTANT) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.QuestionAnswer,
                        contentDescription = null,
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ask Q&A",
                        color = ForestGreenPrimary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = {
                        if (!addedToCrops) {
                            viewModel.addCrop(
                                name = diag.cropGuess,
                                variety = "Monitored",
                                plantedDate = "Today",
                                location = "Inspected Plot",
                                notes = "Diagnosed with ${diag.issueTitle}"
                            )
                            addedToCrops = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (addedToCrops) Icons.Filled.Check else Icons.Filled.Add,
                        contentDescription = null,
                        tint = if (addedToCrops) ForestGreenPrimary else TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (addedToCrops) "Saved" else strings.addToMyCrops,
                        color = if (addedToCrops) ForestGreenPrimary else TextPrimary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "AgriCare Plant Assessment:\nCrop: ${diag.cropGuess}\nLikely Issue: ${diag.issueTitle} (${diag.confidencePercent}%)\nSymptoms: ${diag.visibleSymptoms.joinToString()}"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Crop Assessment"))
                    },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Agricultural extension escalation advice
            if (diag.escalationAdvice.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AmberWarningBg)
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = diag.escalationAdvice,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF7E4B15),
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Disclaimer Banner
            Text(
                text = strings.reportDisclaimer,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
