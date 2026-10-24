package com.example.agricare.data.repository

import com.example.R
import com.example.agricare.data.local.AgriCareDao
import com.example.agricare.data.local.CropTaskEntity
import com.example.agricare.data.local.DiagnosisRecordEntity
import com.example.agricare.data.local.NotificationEntity
import com.example.agricare.data.local.PlantCropEntity
import com.example.agricare.data.model.CropGuide
import com.example.agricare.data.model.CropLibraryData
import com.example.agricare.data.model.CropTask
import com.example.agricare.data.model.DiagnosisResult
import com.example.agricare.data.model.NotificationItem
import com.example.agricare.data.model.PlantCrop
import com.example.agricare.data.model.TreatmentStepItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class AgriCareRepository(private val dao: AgriCareDao) {

    val cropsFlow: Flow<List<PlantCrop>> = dao.getAllCrops().map { list ->
        list.map { entity ->
            PlantCrop(
                id = entity.id,
                name = entity.name,
                variety = entity.variety,
                plantedDate = entity.plantedDate,
                fieldLocation = entity.fieldLocation,
                notes = entity.notes,
                photoRes = entity.photoRes,
                photoUri = entity.photoUri,
                isArchived = entity.isArchived,
                createdAtMillis = entity.createdAtMillis
            )
        }
    }

    val diagnosesFlow: Flow<List<DiagnosisResult>> = dao.getAllDiagnoses().map { list ->
        list.map { entity -> entityToDiagnosis(entity) }
    }

    val tasksFlow: Flow<List<CropTask>> = dao.getAllTasks().map { list ->
        list.map { entity ->
            CropTask(
                id = entity.id,
                title = entity.title,
                timeOfDay = entity.timeOfDay,
                durationText = entity.durationText,
                cropName = entity.cropName,
                dateText = entity.dateText,
                isCompleted = entity.isCompleted,
                isUpcoming = entity.isUpcoming,
                daysAwayText = entity.daysAwayText,
                reminderEnabled = entity.reminderEnabled,
                createdAtMillis = entity.createdAtMillis
            )
        }
    }

    val notificationsFlow: Flow<List<NotificationItem>> = dao.getAllNotifications().map { list ->
        list.map { entity ->
            NotificationItem(
                id = entity.id,
                title = entity.title,
                body = entity.body,
                category = entity.category,
                timeAgo = entity.timeAgo,
                isRead = entity.isRead,
                timestampMillis = entity.timestampMillis
            )
        }
    }

    val cropLibrary: List<CropGuide> = CropLibraryData.curatedGuides

    suspend fun initializeStarterDataIfEmpty() {
        val existingCrops = dao.getAllCrops().firstOrNull()
        if (existingCrops.isNullOrEmpty()) {
            val starterCrops = listOf(
                PlantCropEntity(
                    id = "crop_rice_1",
                    name = "Rice",
                    variety = "BRRI dhan-28",
                    plantedDate = "12 Aug 2024",
                    fieldLocation = "North Plot - 1",
                    notes = "Transplanted healthy seedlings. Good vegetative stage.",
                    photoUri = null,
                    photoRes = R.drawable.rice_blast_leaf,
                    isArchived = false,
                    createdAtMillis = System.currentTimeMillis() - 60L * 86400000L
                ),
                PlantCropEntity(
                    id = "crop_tomato_1",
                    name = "Tomato",
                    variety = "Local",
                    plantedDate = "5 Sep 2024",
                    fieldLocation = "Kitchen Garden",
                    notes = "Flowering started. Monitoring for early blight.",
                    photoUri = null,
                    photoRes = R.drawable.sprout_seedling,
                    isArchived = false,
                    createdAtMillis = System.currentTimeMillis() - 40L * 86400000L
                ),
                PlantCropEntity(
                    id = "crop_potato_1",
                    name = "Potato",
                    variety = "Diamant",
                    plantedDate = "20 Oct 2024",
                    fieldLocation = "Plot 3 - Ridge Row",
                    notes = "Planted certified seed tubers. Proper hilling done.",
                    photoUri = null,
                    photoRes = R.drawable.farmer_field,
                    isArchived = false,
                    createdAtMillis = System.currentTimeMillis() - 20L * 86400000L
                ),
                PlantCropEntity(
                    id = "crop_eggplant_1",
                    name = "Eggplant",
                    variety = "Local",
                    plantedDate = "1 Nov 2024",
                    fieldLocation = "Plot 2",
                    notes = "Checked for shoot borer. Applied pheromone trap.",
                    photoUri = null,
                    photoRes = R.drawable.sprout_seedling,
                    isArchived = false,
                    createdAtMillis = System.currentTimeMillis() - 10L * 86400000L
                ),
                PlantCropEntity(
                    id = "crop_chili_1",
                    name = "Chili",
                    variety = "Local",
                    plantedDate = "15 Nov 2024",
                    fieldLocation = "South Plot",
                    notes = "Bed preparation completed and seedlings established.",
                    photoUri = null,
                    photoRes = R.drawable.rice_blast_leaf,
                    isArchived = false,
                    createdAtMillis = System.currentTimeMillis() - 5L * 86400000L
                )
            )
            dao.insertCrops(starterCrops)
        }

        val existingDiagnoses = dao.getAllDiagnoses().firstOrNull()
        if (existingDiagnoses.isNullOrEmpty()) {
            val starterDiagnoses = listOf(
                createDiagnosisEntity(
                    id = "diag_rice_blast",
                    cropGuess = "Rice",
                    issueTitle = "Rice Blast",
                    scientificName = "Magnaporthe oryzae",
                    assessmentLevel = "likely",
                    confidencePercent = 92,
                    description = "This looks like rice blast disease based on the symptoms in your image.",
                    category = "Fungus",
                    symptoms = listOf(
                        "Spindle-shaped lesions on leaves",
                        "Yellowing around the spots",
                        "Grayish center with dark border"
                    ),
                    alternativeCauses = listOf("Nutrient deficiency (less likely)", "Brown spot"),
                    immediateSteps = listOf(
                        TreatmentStepItem("s1", 1, "Remove severely affected leaves", "Cut and destroy infected parts to limit spore dispersal.", "now", true),
                        TreatmentStepItem("s2", 2, "Use recommended fungicide", "Carbendazim (0.1%) or Tricyclazole (0.6 mL/L water).", "now", false),
                        TreatmentStepItem("s3", 3, "Maintain proper water level", "Avoid water stagnation in the field.", "soon", false),
                        TreatmentStepItem("s4", 4, "Ensure good air circulation", "Keep proper spacing between plants.", "soon", false),
                        TreatmentStepItem("s5", 5, "Follow crop rotation", "Avoid planting rice in the same field for consecutive seasons.", "monitor", false)
                    ),
                    prevention = listOf(
                        "Use certified disease-resistant seeds",
                        "Avoid excess nitrogen fertilizer",
                        "Ensure field sanitation"
                    ),
                    escalationAdvice = "If symptoms spread rapidly to more than 20% of your crop, take a leaf sample to your local Upazila Agriculture Officer.",
                    timestampMillis = System.currentTimeMillis() - 2 * 3600000L,
                    imageRes = R.drawable.rice_blast_leaf
                ),
                createDiagnosisEntity(
                    id = "diag_tomato_leaf_mold",
                    cropGuess = "Tomato",
                    issueTitle = "Tomato Leaf Mold",
                    scientificName = "Passalora fulva",
                    assessmentLevel = "likely",
                    confidencePercent = 87,
                    description = "Pale green or yellowish spots on upper leaf surfaces with olive green mold underneath.",
                    category = "Fungus",
                    symptoms = listOf(
                        "Pale yellow spots on leaf surface",
                        "Velvety olive-green downy mold on underside",
                        "Lower leaves curling and withering"
                    ),
                    alternativeCauses = listOf("Powdery mildew", "Early blight"),
                    immediateSteps = listOf(
                        TreatmentStepItem("t1", 1, "Prune lower foliage", "Increase air circulation around stems.", "now", true),
                        TreatmentStepItem("t2", 2, "Reduce greenhouse / tunnel humidity", "Ventilate shelter early in the morning.", "soon", false),
                        TreatmentStepItem("t3", 3, "Apply protective bio-fungicide", "Spray copper oxychloride according to local guidance.", "soon", false)
                    ),
                    prevention = listOf("Keep foliage dry during watering", "Use resistant tomato varieties"),
                    escalationAdvice = "Consult local agronomist if leaves drop prematurely before flowering.",
                    timestampMillis = System.currentTimeMillis() - 17L * 86400000L,
                    imageRes = R.drawable.sprout_seedling
                ),
                createDiagnosisEntity(
                    id = "diag_potato_early_blight",
                    cropGuess = "Potato",
                    issueTitle = "Potato Early Blight",
                    scientificName = "Alternaria solani",
                    assessmentLevel = "likely",
                    confidencePercent = 76,
                    description = "Concentric dark brown target-like rings on older lower leaves.",
                    category = "Fungus",
                    symptoms = listOf(
                        "Concentric brown rings",
                        "Yellow halo around lesions",
                        "Premature defoliation"
                    ),
                    alternativeCauses = listOf("Magnesium deficiency", "Late blight"),
                    immediateSteps = listOf(
                        TreatmentStepItem("p1", 1, "Prune infected lower foliage", "Dispose away from fields.", "now", true),
                        TreatmentStepItem("p2", 2, "Apply Mancozeb spray", "Spray at recommended agricultural dilution.", "now", false)
                    ),
                    prevention = listOf("Crop rotation with non-solanaceous crops", "Mulch ridges to avoid splash"),
                    escalationAdvice = "Seek advice if lesions spread to stems and tubers.",
                    timestampMillis = System.currentTimeMillis() - 25L * 86400000L,
                    imageRes = R.drawable.farmer_field
                ),
                createDiagnosisEntity(
                    id = "diag_aphids_chili",
                    cropGuess = "Chili",
                    issueTitle = "Aphids (Chili)",
                    scientificName = "Aphis gossypii",
                    assessmentLevel = "likely",
                    confidencePercent = 68,
                    description = "Small green/black sucking insects clustering under leaves causing curling and sticky honeydew.",
                    category = "Pest",
                    symptoms = listOf(
                        "Curled and stunted leaf tips",
                        "Sticky honeydew and black sooty mold",
                        "Presence of ant activity on plants"
                    ),
                    alternativeCauses = listOf("Thrips infestation", "Chili leaf curl virus"),
                    immediateSteps = listOf(
                        TreatmentStepItem("a1", 1, "Water blast wash", "Spray strong jet of water under leaves.", "now", true),
                        TreatmentStepItem("a2", 2, "Apply Neem oil solution", "5 mL neem oil + 2 mL liquid soap per liter.", "soon", false)
                    ),
                    prevention = listOf("Install yellow sticky traps", "Encourage natural ladybird predators"),
                    escalationAdvice = "Consult extension agent if virus transmission begins.",
                    timestampMillis = System.currentTimeMillis() - 33L * 86400000L,
                    imageRes = R.drawable.rice_blast_leaf
                )
            )
            dao.insertDiagnoses(starterDiagnoses)
        }

        val existingTasks = dao.getAllTasks().firstOrNull()
        if (existingTasks.isNullOrEmpty()) {
            val starterTasks = listOf(
                CropTaskEntity(
                    id = "task_pest_tomato",
                    title = "Check for pest in tomato plants",
                    timeOfDay = "Morning",
                    durationText = "30 min",
                    cropName = "Tomato",
                    dateText = "Today",
                    isCompleted = true,
                    isUpcoming = false,
                    daysAwayText = "",
                    reminderEnabled = true,
                    createdAtMillis = System.currentTimeMillis() - 3600000L * 4
                ),
                CropTaskEntity(
                    id = "task_fertilizer_rice",
                    title = "Apply fertilizer to rice field",
                    timeOfDay = "Afternoon",
                    durationText = "1 hr",
                    cropName = "Rice",
                    dateText = "Today",
                    isCompleted = false,
                    isUpcoming = false,
                    daysAwayText = "",
                    reminderEnabled = true,
                    createdAtMillis = System.currentTimeMillis() - 3600000L * 2
                ),
                CropTaskEntity(
                    id = "task_water_seedlings",
                    title = "Water the seedlings",
                    timeOfDay = "Evening",
                    durationText = "30 min",
                    cropName = "Chili",
                    dateText = "Today",
                    isCompleted = false,
                    isUpcoming = false,
                    daysAwayText = "",
                    reminderEnabled = true,
                    createdAtMillis = System.currentTimeMillis() - 3600000L
                ),
                CropTaskEntity(
                    id = "task_harvest_rice",
                    title = "Harvesting (Rice)",
                    timeOfDay = "Morning",
                    durationText = "BRRI dhan-28",
                    cropName = "Rice",
                    dateText = "Nov 25, 2024",
                    isCompleted = false,
                    isUpcoming = true,
                    daysAwayText = "in 12 days",
                    reminderEnabled = true,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
            dao.insertTasks(starterTasks)
        }

        val existingNotifs = dao.getAllNotifications().firstOrNull()
        if (existingNotifs.isNullOrEmpty()) {
            val starterNotifs = listOf(
                NotificationEntity(
                    id = "notif_1",
                    title = "Early blight risk in tomato",
                    body = "Weather conditions are favorable for fungal development. Inspect lower leaves today.",
                    category = "Alerts",
                    timeAgo = "2h ago",
                    isRead = false,
                    timestampMillis = System.currentTimeMillis() - 7200000L
                ),
                NotificationEntity(
                    id = "notif_2",
                    title = "Treatment reminder",
                    body = "Apply fungicide to rice field as scheduled in your Rice Blast plan.",
                    category = "Reminders",
                    timeAgo = "Yesterday",
                    isRead = false,
                    timestampMillis = System.currentTimeMillis() - 86400000L
                ),
                NotificationEntity(
                    id = "notif_3",
                    title = "New guide available",
                    body = "Tomato farming best practices from BARI agronomists.",
                    category = "Tips",
                    timeAgo = "2 days ago",
                    isRead = true,
                    timestampMillis = System.currentTimeMillis() - 2 * 86400000L
                ),
                NotificationEntity(
                    id = "notif_4",
                    title = "Crop calendar",
                    body = "Your rice crop is approaching maturity. Prepare harvesting equipment.",
                    category = "Reminders",
                    timeAgo = "3 days ago",
                    isRead = true,
                    timestampMillis = System.currentTimeMillis() - 3 * 86400000L
                ),
                NotificationEntity(
                    id = "notif_5",
                    title = "Plant care tip",
                    body = "Maintain soil moisture for better yield during grain-filling stage.",
                    category = "Tips",
                    timeAgo = "4 days ago",
                    isRead = true,
                    timestampMillis = System.currentTimeMillis() - 4 * 86400000L
                )
            )
            dao.insertNotifications(starterNotifs)
        }
    }

    suspend fun addCrop(crop: PlantCrop) {
        dao.insertCrop(
            PlantCropEntity(
                id = crop.id,
                name = crop.name,
                variety = crop.variety,
                plantedDate = crop.plantedDate,
                fieldLocation = crop.fieldLocation,
                notes = crop.notes,
                photoUri = crop.photoUri,
                photoRes = crop.photoRes,
                isArchived = crop.isArchived,
                createdAtMillis = crop.createdAtMillis
            )
        )
    }

    suspend fun deleteCrop(cropId: String) {
        val crops = dao.getAllCrops().firstOrNull() ?: emptyList()
        val found = crops.find { it.id == cropId }
        if (found != null) {
            dao.deleteCrop(found)
        }
    }

    suspend fun saveDiagnosis(diagnosis: DiagnosisResult) {
        dao.insertDiagnosis(diagnosisToEntity(diagnosis))
    }

    suspend fun deleteDiagnosis(id: String) {
        dao.deleteDiagnosisById(id)
    }

    suspend fun clearAllDiagnoses() {
        dao.clearAllDiagnoses()
    }

    suspend fun addTask(task: CropTask) {
        dao.insertTask(
            CropTaskEntity(
                id = task.id,
                title = task.title,
                timeOfDay = task.timeOfDay,
                durationText = task.durationText,
                cropName = task.cropName,
                dateText = task.dateText,
                isCompleted = task.isCompleted,
                isUpcoming = task.isUpcoming,
                daysAwayText = task.daysAwayText,
                reminderEnabled = task.reminderEnabled,
                createdAtMillis = task.createdAtMillis
            )
        )
    }

    suspend fun toggleTaskCompleted(taskId: String, completed: Boolean) {
        val tasks = dao.getAllTasks().firstOrNull() ?: emptyList()
        val found = tasks.find { it.id == taskId }
        if (found != null) {
            dao.updateTask(found.copy(isCompleted = completed))
        }
    }

    suspend fun deleteTask(taskId: String) {
        val tasks = dao.getAllTasks().firstOrNull() ?: emptyList()
        val found = tasks.find { it.id == taskId }
        if (found != null) {
            dao.deleteTask(found)
        }
    }

    suspend fun markNotificationAsRead(id: String) {
        dao.markNotificationRead(id)
    }

    suspend fun clearAllNotifications() {
        dao.clearAllNotifications()
    }

    suspend fun clearAllData() {
        dao.clearAllCrops()
        dao.clearAllDiagnoses()
        dao.clearAllTasks()
        dao.clearAllNotifications()
    }

    private fun entityToDiagnosis(entity: DiagnosisRecordEntity): DiagnosisResult {
        val symptoms = parseJsonStringList(entity.visibleSymptomsJson)
        val alternatives = parseJsonStringList(entity.alternativeCausesJson)
        val immediate = parseJsonSteps(entity.immediateStepsJson)
        val prevention = parseJsonStringList(entity.preventionStepsJson)
        val questions = parseJsonStringList(entity.followUpQuestionsJson)
        val safety = parseJsonStringList(entity.safetyNotesJson)

        return DiagnosisResult(
            id = entity.id,
            cropGuess = entity.cropGuess,
            issueTitle = entity.issueTitle,
            scientificName = entity.scientificName,
            assessmentLevel = entity.assessmentLevel,
            confidencePercent = entity.confidencePercent,
            description = entity.description,
            category = entity.category,
            visibleSymptoms = symptoms,
            alternativeCauses = alternatives,
            immediateSteps = immediate,
            preventionSteps = prevention,
            followUpQuestions = questions,
            escalationAdvice = entity.escalationAdvice,
            safetyNotes = safety,
            limitations = entity.limitations,
            language = entity.language,
            timestampMillis = entity.timestampMillis,
            imageUri = entity.imageUri,
            imageRes = entity.imageRes
        )
    }

    private fun diagnosisToEntity(diag: DiagnosisResult): DiagnosisRecordEntity {
        return DiagnosisRecordEntity(
            id = diag.id,
            cropGuess = diag.cropGuess,
            issueTitle = diag.issueTitle,
            scientificName = diag.scientificName,
            assessmentLevel = diag.assessmentLevel,
            confidencePercent = diag.confidencePercent,
            description = diag.description,
            category = diag.category,
            visibleSymptomsJson = listToJsonString(diag.visibleSymptoms),
            alternativeCausesJson = listToJsonString(diag.alternativeCauses),
            immediateStepsJson = stepsToJsonString(diag.immediateSteps),
            preventionStepsJson = listToJsonString(diag.preventionSteps),
            followUpQuestionsJson = listToJsonString(diag.followUpQuestions),
            escalationAdvice = diag.escalationAdvice,
            safetyNotesJson = listToJsonString(diag.safetyNotes),
            limitations = diag.limitations,
            language = diag.language,
            timestampMillis = diag.timestampMillis,
            imageUri = diag.imageUri,
            imageRes = diag.imageRes
        )
    }

    private fun createDiagnosisEntity(
        id: String,
        cropGuess: String,
        issueTitle: String,
        scientificName: String,
        assessmentLevel: String,
        confidencePercent: Int,
        description: String,
        category: String,
        symptoms: List<String>,
        alternativeCauses: List<String>,
        immediateSteps: List<TreatmentStepItem>,
        prevention: List<String>,
        escalationAdvice: String,
        timestampMillis: Long,
        imageRes: Int?
    ): DiagnosisRecordEntity {
        return DiagnosisRecordEntity(
            id = id,
            cropGuess = cropGuess,
            issueTitle = issueTitle,
            scientificName = scientificName,
            assessmentLevel = assessmentLevel,
            confidencePercent = confidencePercent,
            description = description,
            category = category,
            visibleSymptomsJson = listToJsonString(symptoms),
            alternativeCausesJson = listToJsonString(alternativeCauses),
            immediateStepsJson = stepsToJsonString(immediateSteps),
            preventionStepsJson = listToJsonString(prevention),
            followUpQuestionsJson = listToJsonString(listOf("When did you first notice these spots?", "Has there been heavy rain recently?")),
            escalationAdvice = escalationAdvice,
            safetyNotesJson = listToJsonString(listOf("Wear protective gloves and mask when applying treatments.")),
            limitations = "Visual assessment only. Laboratory confirmation recommended for widespread infection.",
            language = "en",
            timestampMillis = timestampMillis,
            imageUri = null,
            imageRes = imageRes
        )
    }

    private fun listToJsonString(list: List<String>): String {
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    private fun parseJsonStringList(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { array.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun stepsToJsonString(steps: List<TreatmentStepItem>): String {
        val array = JSONArray()
        steps.forEach { step ->
            val obj = JSONObject()
            obj.put("id", step.id)
            obj.put("orderIndex", step.orderIndex)
            obj.put("title", step.title)
            obj.put("description", step.description)
            obj.put("priority", step.priority)
            obj.put("isCompleted", step.isCompleted)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseJsonSteps(json: String): List<TreatmentStepItem> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { idx ->
                val obj = array.getJSONObject(idx)
                TreatmentStepItem(
                    id = obj.optString("id", "step_$idx"),
                    orderIndex = obj.optInt("orderIndex", idx + 1),
                    title = obj.getString("title"),
                    description = obj.getString("description"),
                    priority = obj.optString("priority", "now"),
                    isCompleted = obj.optBoolean("isCompleted", false)
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
