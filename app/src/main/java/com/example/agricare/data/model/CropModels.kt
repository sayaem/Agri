package com.example.agricare.data.model

data class PlantCrop(
    val id: String,
    val name: String,
    val variety: String = "",
    val plantedDate: String = "",
    val fieldLocation: String = "",
    val notes: String = "",
    val photoRes: Int? = null,
    val photoUri: String? = null,
    val isArchived: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class DiagnosisResult(
    val id: String,
    val cropGuess: String,
    val issueTitle: String,
    val scientificName: String = "",
    val assessmentLevel: String = "likely", // likely, uncertain, insufficient_image
    val confidencePercent: Int = 92,
    val description: String = "",
    val category: String = "Fungal Disease", // Fungal Disease, Pest, Bacterial, Viral, Deficiency
    val visibleSymptoms: List<String> = emptyList(),
    val alternativeCauses: List<String> = emptyList(),
    val immediateSteps: List<TreatmentStepItem> = emptyList(),
    val preventionSteps: List<String> = emptyList(),
    val followUpQuestions: List<String> = emptyList(),
    val escalationAdvice: String = "",
    val safetyNotes: List<String> = emptyList(),
    val limitations: String = "",
    val language: String = "en",
    val timestampMillis: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val imageRes: Int? = null
)

data class TreatmentStepItem(
    val id: String,
    val orderIndex: Int,
    val title: String,
    val description: String,
    val priority: String = "now", // now, soon, monitor
    val isCompleted: Boolean = false
)

data class CropTask(
    val id: String,
    val title: String,
    val timeOfDay: String = "Morning",
    val durationText: String = "30 min",
    val cropName: String = "",
    val dateText: String = "Today",
    val isCompleted: Boolean = false,
    val isUpcoming: Boolean = false,
    val daysAwayText: String = "",
    val reminderEnabled: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val category: String = "Tips", // Tips, Reminders, Alerts, All
    val timeAgo: String = "Just now",
    val isRead: Boolean = false,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class CropGuide(
    val id: String,
    val cropName: String,
    val diseaseTitle: String,
    val scientificName: String,
    val category: String,
    val summary: String,
    val symptoms: List<String>,
    val similarProblems: List<String>,
    val safeManagement: List<String>,
    val prevention: List<String>,
    val imageRes: Int? = null,
    val sourceAttribution: String = "Bangladesh Rice Research Institute (BRRI) & DAE Guidelines"
)
