package com.example.agricare.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agricare.data.gemini.GeminiAnalysisState
import com.example.agricare.data.gemini.GeminiVisionService
import com.example.agricare.data.local.AgriCareDatabase
import com.example.agricare.data.model.CropGuide
import com.example.agricare.data.model.CropTask
import com.example.agricare.data.model.DiagnosisResult
import com.example.agricare.data.model.NotificationItem
import com.example.agricare.data.model.PlantCrop
import com.example.agricare.data.model.TreatmentStepItem
import com.example.agricare.data.preferences.AgriCarePreferences
import com.example.agricare.data.repository.AgriCareRepository
import com.example.agricare.localization.AppLanguage
import com.example.agricare.localization.LocalizedStrings
import com.example.agricare.localization.getStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    MAIN_HOME,
    DIAGNOSE_CAMERA,
    ANALYSIS_LOADING,
    ANALYSIS_RESULT,
    TREATMENT_PLAN,
    CROP_DETAIL,
    ADD_CROP,
    NOTIFICATIONS,
    VOICE_ASSISTANT,
    SETTINGS,
    ABOUT_PRIVACY
}

enum class MainTab {
    HOME,
    MY_CROPS,
    DIAGNOSE,
    CALENDAR,
    PROFILE
}

class AgriCareViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AgriCareDatabase.getDatabase(application)
    private val repository = AgriCareRepository(database.dao())
    private val preferences = AgriCarePreferences(application)
    private val geminiService = GeminiVisionService(application)

    // Current Screen Navigation Stack
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Preferences & Localization State
    val language: StateFlow<AppLanguage> = preferences.languageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppLanguage.ENGLISH)

    val themeMode: StateFlow<String> = preferences.themeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "light")

    val isOnboardingCompleted: StateFlow<Boolean> = preferences.onboardingCompletedFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val userName: StateFlow<String> = preferences.userNameFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Sayed")

    val userRole: StateFlow<String> = preferences.userRoleFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Plant Lover · Farmer")

    val customApiKey: StateFlow<String> = preferences.customApiKeyFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    // Database Collections
    val crops: StateFlow<List<PlantCrop>> = repository.cropsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val diagnoses: StateFlow<List<DiagnosisResult>> = repository.diagnosesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<CropTask>> = repository.tasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cropLibrary: List<CropGuide> = repository.cropLibrary

    // Active Diagnosis Session State
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _analysisState = MutableStateFlow<GeminiAnalysisState>(GeminiAnalysisState.Idle)
    val analysisState: StateFlow<GeminiAnalysisState> = _analysisState.asStateFlow()

    private val _activeDiagnosis = MutableStateFlow<DiagnosisResult?>(null)
    val activeDiagnosis: StateFlow<DiagnosisResult?> = _activeDiagnosis.asStateFlow()

    private val _activeCropGuide = MutableStateFlow<CropGuide?>(null)
    val activeCropGuide: StateFlow<CropGuide?> = _activeCropGuide.asStateFlow()

    // Q&A / Voice State
    private val _voiceChatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "Hello! I am AgriCare. Ask me anything about crop diseases, pest controls, or seasonal cultivation." to false
        )
    )
    val voiceChatMessages: StateFlow<List<Pair<String, Boolean>>> = _voiceChatMessages.asStateFlow()

    val strings: LocalizedStrings
        get() = getStrings(language.value)

    init {
        viewModelScope.launch {
            repository.initializeStarterDataIfEmpty()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectTab(tab: MainTab) {
        if (tab == MainTab.DIAGNOSE) {
            _currentScreen.value = AppScreen.DIAGNOSE_CAMERA
        } else {
            _currentTab.value = tab
            _currentScreen.value = AppScreen.MAIN_HOME
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferences.setOnboardingCompleted(true)
            _currentScreen.value = AppScreen.MAIN_HOME
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            preferences.setOnboardingCompleted(false)
            _currentScreen.value = AppScreen.ONBOARDING
        }
    }

    fun setLanguage(lang: AppLanguage) {
        viewModelScope.launch {
            preferences.setLanguage(lang)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferences.setTheme(mode)
        }
    }

    fun setCustomApiKey(key: String) {
        viewModelScope.launch {
            preferences.setCustomApiKey(key)
        }
    }

    fun setImageForDiagnosis(uri: Uri?, bitmap: Bitmap?) {
        _selectedImageUri.value = uri
        _selectedBitmap.value = bitmap
        _analysisState.value = GeminiAnalysisState.Idle
    }

    fun startAnalysis(cropHint: String? = null) {
        _currentScreen.value = AppScreen.ANALYSIS_LOADING
        _analysisState.value = GeminiAnalysisState.Loading("Preparing scan...")

        viewModelScope.launch {
            val state = geminiService.analyzePlantImage(
                imageUri = _selectedImageUri.value,
                bitmapDirect = _selectedBitmap.value,
                userCropHint = cropHint,
                languageCode = language.value.code,
                customApiKey = customApiKey.value
            )
            _analysisState.value = state
            if (state is GeminiAnalysisState.Success) {
                _activeDiagnosis.value = state.result
                repository.saveDiagnosis(state.result)
                _currentScreen.value = AppScreen.ANALYSIS_RESULT
            }
        }
    }

    fun loadDemoRiceBlastDiagnosis() {
        viewModelScope.launch {
            val starterList = diagnoses.value
            val blast = starterList.find { it.id == "diag_rice_blast" } ?: starterList.firstOrNull()
            if (blast != null) {
                _activeDiagnosis.value = blast
                _currentScreen.value = AppScreen.ANALYSIS_RESULT
            }
        }
    }

    fun openDiagnosisDetail(diag: DiagnosisResult) {
        _activeDiagnosis.value = diag
        _currentScreen.value = AppScreen.ANALYSIS_RESULT
    }

    fun openCropGuide(guide: CropGuide) {
        _activeCropGuide.value = guide
        _currentScreen.value = AppScreen.CROP_DETAIL
    }

    fun toggleTreatmentStep(stepId: String) {
        val current = _activeDiagnosis.value ?: return
        val updatedSteps = current.immediateSteps.map { step ->
            if (step.id == stepId) step.copy(isCompleted = !step.isCompleted) else step
        }
        val updatedDiag = current.copy(immediateSteps = updatedSteps)
        _activeDiagnosis.value = updatedDiag
        viewModelScope.launch {
            repository.saveDiagnosis(updatedDiag)
        }
    }

    fun addCrop(name: String, variety: String, plantedDate: String, location: String, notes: String) {
        viewModelScope.launch {
            val newCrop = PlantCrop(
                id = "crop_${UUID.randomUUID()}",
                name = name,
                variety = variety,
                plantedDate = plantedDate.ifBlank { "Recent" },
                fieldLocation = location,
                notes = notes,
                photoRes = null,
                photoUri = null
            )
            repository.addCrop(newCrop)
        }
    }

    fun deleteCrop(cropId: String) {
        viewModelScope.launch {
            repository.deleteCrop(cropId)
        }
    }

    fun addTask(title: String, timeOfDay: String, cropName: String, duration: String) {
        viewModelScope.launch {
            val task = CropTask(
                id = "task_${UUID.randomUUID()}",
                title = title,
                timeOfDay = timeOfDay,
                durationText = duration.ifBlank { "30 min" },
                cropName = cropName,
                dateText = "Today",
                isCompleted = false
            )
            repository.addTask(task)
        }
    }

    fun toggleTask(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(taskId, isCompleted)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun deleteDiagnosis(id: String) {
        viewModelScope.launch {
            repository.deleteDiagnosis(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAllDiagnoses()
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            preferences.clearAllPreferences()
            _currentScreen.value = AppScreen.ONBOARDING
        }
    }

    fun askVoiceQuestion(question: String) {
        if (question.isBlank()) return
        val currentList = _voiceChatMessages.value.toMutableList()
        currentList.add(question to true)
        _voiceChatMessages.value = currentList

        viewModelScope.launch {
            // Intelligent agricultural response based on query
            val reply = generateAgriculturalAnswer(question, language.value)
            val updated = _voiceChatMessages.value.toMutableList()
            updated.add(reply to false)
            _voiceChatMessages.value = updated
        }
    }

    private fun generateAgriculturalAnswer(q: String, lang: AppLanguage): String {
        val lower = q.lowercase()
        return if (lang == AppLanguage.BANGLA) {
            when {
                lower.contains("blast") || lower.contains("ব্লাস্ট") || lower.contains("ধান") ->
                    "ধsession ব্লাস্ট দমনে ট্রাইসাইক্লাজল ৭৫ ডব্লিউপি বা কার্বেনডাজিম অনুমোদিত মাত্রায় প্রয়োগ করুন। জমিতে অতিরিক্ত ইউরিয়া সার ব্যবহার পরিহার করুন এবং পর্যাপ্ত বাতাস চলাচলের ব্যবস্থা রাখুন।"
                lower.contains("পোকা") || lower.contains("insect") || lower.contains("aphid") ->
                    "পোকা দমনে প্রাথমিকভাবে নিমের তেল স্প্রে করতে পারেন (প্রতি লিটার পানিতে ৫ মিলি নিম তেল ও অল্প সাবান গুঁড়া)। অতিরিক্ত আক্রমণে অনুমোদিত কীটনাশক ব্যবহার করুন।"
                lower.contains("টমেটো") || lower.contains("tomato") ->
                    "টমেটোর পাতা কুঁকড়ে যাওয়া বা আর্লি ব্লাইট প্রতিরোধে আক্রান্ত পাতা ছিঁড়ে পুড়িয়ে ফেলুন। ছত্রাকনাশক হিসেবে ম্যানকোজেব ব্যবহার করতে পারেন।"
                else ->
                    "আপনার প্রশ্নের জন্য ধন্যবাদ। লক্ষণযুক্ত পাতার স্পষ্ট ছবি তুলে 'রোগ নির্ণয়' অপশনে পরীক্ষা করুন অথবা বিস্তারিত তথ্যের জন্য নিকটস্থ উপজেলা কৃষি অফিসে যোগাযোগ করুন।"
            }
        } else {
            when {
                lower.contains("blast") || lower.contains("rice") ->
                    "For rice blast, maintain 2-3 inches of standing water, avoid excessive nitrogen, and consider applying approved fungicides such as Tricyclazole 75 WP at 0.6g/L water early in the morning."
                lower.contains("pest") || lower.contains("aphid") || lower.contains("insect") ->
                    "For aphid and pest control, start with physical washes or organic Neem oil spray (5ml per liter with mild soap). Install yellow sticky traps around the field perimeter."
                lower.contains("tomato") || lower.contains("blight") ->
                    "For tomato blight, prune lower leaves touching the soil, water at the base to keep foliage dry, and spray copper-based fungicide or Mancozeb as recommended by local labels."
                else ->
                    "For detailed diagnosis, please take a clear photo using the Diagnose scanner. Always verify chemical rates with your local Upazila Agriculture Officer."
            }
        }
    }
}
