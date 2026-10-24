package com.example.agricare.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agricare.data.gemini.GeminiAnalysisState
import com.example.agricare.ui.AppScreen
import com.example.agricare.ui.AgriCareViewModel
import com.example.agricare.ui.components.AgriCareCard
import com.example.agricare.ui.components.AgriCarePrimaryButton
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.RedError
import com.example.ui.theme.SageBackground
import com.example.ui.theme.SageContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun AnalysisLoadingScreen(viewModel: AgriCareViewModel) {
    val strings = viewModel.strings
    val state by viewModel.analysisState.collectAsState()

    var stageIndex by remember { mutableIntStateOf(0) }
    val stages = listOf(
        strings.stepPreparing,
        strings.stepExamining,
        strings.stepGuidance
    )

    LaunchedEffect(state) {
        if (state is GeminiAnalysisState.Loading) {
            stageIndex = 0
            delay(1500)
            stageIndex = 1
            delay(2000)
            stageIndex = 2
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SageBackground)
            .statusBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is GeminiAnalysisState.Loading, GeminiAnalysisState.Idle -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(SageContainer.copy(alpha = 0.6f))
                        )
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(ForestGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                        CircularProgressIndicator(
                            modifier = Modifier.size(136.dp),
                            color = ForestGreenPrimary,
                            strokeWidth = 3.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = strings.analyzingTitle,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stages.getOrElse(stageIndex) { stages.last() },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 14.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // 3 visual steps indicators
                    Column(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        stages.forEachIndexed { index, stepText ->
                            val isDone = index < stageIndex
                            val isCurrent = index == stageIndex

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = if (isDone) ForestGreenPrimary else if (isCurrent) ForestGreenDark else TextSecondary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stepText,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDone || isCurrent) TextPrimary else TextSecondary.copy(alpha = 0.5f),
                                        fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    TextButton(onClick = { viewModel.navigateTo(AppScreen.MAIN_HOME) }) {
                        Text(
                            text = strings.cancel,
                            style = MaterialTheme.typography.labelLarge.copy(color = TextSecondary)
                        )
                    }
                }
            }

            is GeminiAnalysisState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEBEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = RedError,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Diagnosis Unavailable",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentState.message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    if (currentState.canRetry) {
                        AgriCarePrimaryButton(
                            text = strings.retry,
                            onClick = { viewModel.startAnalysis() },
                            leadingIcon = Icons.Filled.Refresh
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Open offline library fallback action (PRD Section 6.7)
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.CROP_DETAIL) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.MenuBook, contentDescription = null, tint = ForestGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Consult Offline Crop Library",
                            color = ForestGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fallback to Demo preview
                    Button(
                        onClick = { viewModel.loadDemoRiceBlastDiagnosis() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SageContainer,
                            contentColor = ForestGreenDark
                        )
                    ) {
                        Text(
                            text = "Explore Rice Blast Demo Scan",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            is GeminiAnalysisState.Success -> {
                // Routed via ViewModel to ANALYSIS_RESULT
            }
        }
    }
}
