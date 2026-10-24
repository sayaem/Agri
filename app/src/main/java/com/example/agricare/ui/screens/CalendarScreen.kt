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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.agricare.data.model.CropTask
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
fun CalendarScreen(viewModel: AgriCareViewModel) {
    val strings = viewModel.strings
    val tasks by viewModel.tasks.collectAsState()

    var selectedDayIndex by remember { mutableIntStateOf(2) } // Wednesday 13 active
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val days = listOf(
        Pair("Mon", "11"),
        Pair("Tue", "12"),
        Pair("Wed", "13"),
        Pair("Thu", "14"),
        Pair("Fri", "15"),
        Pair("Sat", "16"),
        Pair("Sun", "17")
    )

    val todayTasks = tasks.filter { !it.isUpcoming }
    val upcomingTasks = tasks.filter { it.isUpcoming }

    Scaffold(
        topBar = {
            AgriCareTopBar(
                title = strings.calendarTitle,
                actions = {
                    IconButton(
                        onClick = { showAddTaskDialog = true },
                        modifier = Modifier.testTag("add_task_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = strings.addTask,
                            tint = ForestGreenPrimary
                        )
                    }
                }
            )
        },
        containerColor = SageBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Month Selector Row: < November 2024 >
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "November 2024",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 17.sp
                    )
                )

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = TextPrimary
                    )
                }
            }

            // Days of the week row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { index, (dayName, dateNum) ->
                    val isSelected = index == selectedDayIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) ForestGreenPrimary else Color.Transparent)
                            .clickable { selectedDayIndex = index }
                            .padding(vertical = 10.dp, horizontal = 10.dp)
                    ) {
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dateNum,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // Today's Tasks Header
                item {
                    Text(
                        text = strings.todaysTasks,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (todayTasks.isEmpty()) {
                    item {
                        AgriCareCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = strings.emptyTasks,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(todayTasks) { task ->
                        TaskItemCard(
                            task = task,
                            onToggle = { viewModel.toggleTask(task.id, !task.isCompleted) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Upcoming Section
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = strings.upcomingEvents,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(upcomingTasks) { upcoming ->
                    UpcomingEventCard(task = upcoming)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            strings = strings,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, timeOfDay, crop, duration ->
                viewModel.addTask(title, timeOfDay, crop, duration)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun TaskItemCard(
    task: CropTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    AgriCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .testTag("task_item_${task.id}"),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive circular checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        if (task.isCompleted) ForestGreenPrimary else Color(0xFFBDC7C0),
                        CircleShape
                    )
                    .background(if (task.isCompleted) ForestGreenPrimary else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted) TextSecondary else TextPrimary,
                        fontSize = 14.sp,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${task.timeOfDay} · ${task.durationText}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete task",
                    tint = TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun UpcomingEventCard(task: CropTask) {
    AgriCareCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFFBF4ED),
        borderColor = Color(0xFFF3DFCE)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8E7D8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Event,
                    contentDescription = null,
                    tint = Color(0xFFD35400),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${task.daysAwayText} · ${task.dateText}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    strings: com.example.agricare.localization.LocalizedStrings,
    onDismiss: () -> Unit,
    onConfirm: (title: String, timeOfDay: String, crop: String, duration: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var timeOfDay by remember { mutableStateOf("Morning") }
    var cropName by remember { mutableStateOf("Rice") }
    var duration by remember { mutableStateOf("30 min") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.addTask,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(strings.taskTitle) },
                    placeholder = { Text("e.g. Inspect lower leaves for spots") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = timeOfDay,
                    onValueChange = { timeOfDay = it },
                    label = { Text("Time of Day") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text(strings.taskDueTime) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), timeOfDay.trim(), cropName.trim(), duration.trim())
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
