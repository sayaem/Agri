package com.example.agricare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_crops")
data class PlantCropEntity(
    @PrimaryKey val id: String,
    val name: String,
    val variety: String,
    val plantedDate: String,
    val fieldLocation: String,
    val notes: String,
    val photoUri: String?,
    val photoRes: Int?,
    val isArchived: Boolean,
    val createdAtMillis: Long
)

@Entity(tableName = "diagnosis_records")
data class DiagnosisRecordEntity(
    @PrimaryKey val id: String,
    val cropGuess: String,
    val issueTitle: String,
    val scientificName: String,
    val assessmentLevel: String,
    val confidencePercent: Int,
    val description: String,
    val category: String,
    val visibleSymptomsJson: String,
    val alternativeCausesJson: String,
    val immediateStepsJson: String,
    val preventionStepsJson: String,
    val followUpQuestionsJson: String,
    val escalationAdvice: String,
    val safetyNotesJson: String,
    val limitations: String,
    val language: String,
    val timestampMillis: Long,
    val imageUri: String?,
    val imageRes: Int?
)

@Entity(tableName = "crop_tasks")
data class CropTaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val timeOfDay: String,
    val durationText: String,
    val cropName: String,
    val dateText: String,
    val isCompleted: Boolean,
    val isUpcoming: Boolean,
    val daysAwayText: String,
    val reminderEnabled: Boolean,
    val createdAtMillis: Long
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val category: String,
    val timeAgo: String,
    val isRead: Boolean,
    val timestampMillis: Long
)
