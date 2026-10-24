package com.example.agricare.data.gemini

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.agricare.data.model.DiagnosisResult
import com.example.agricare.data.model.TreatmentStepItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

sealed interface GeminiAnalysisState {
    data object Idle : GeminiAnalysisState
    data class Loading(val stage: String) : GeminiAnalysisState
    data class Success(val result: DiagnosisResult) : GeminiAnalysisState
    data class Error(
        val message: String,
        val canRetry: Boolean = true,
        val isQuotaExceeded: Boolean = false,
        val isOffline: Boolean = false,
        val isApiKeyMissing: Boolean = false
    ) : GeminiAnalysisState
}

class GeminiVisionService(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val TAG = "GeminiVisionService"
        private const val PRIMARY_MODEL = "gemini-2.5-flash"
    }

    suspend fun analyzePlantImage(
        imageUri: Uri?,
        bitmapDirect: Bitmap?,
        userCropHint: String?,
        languageCode: String,
        customApiKey: String? = null
    ): GeminiAnalysisState = withContext(Dispatchers.IO) {
        // 1. Resolve API key
        val key = when {
            !customApiKey.isNullOrBlank() -> customApiKey.trim()
            else -> try {
                val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
                (field.get(null) as? String)?.trim().orEmpty()
            } catch (_: Exception) {
                ""
            }
        }

        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            return@withContext GeminiAnalysisState.Error(
                message = "Gemini API key is not configured. You can provide an API key in Profile > Settings or configure GEMINI_API_KEY in the Secrets panel.",
                canRetry = false,
                isApiKeyMissing = true
            )
        }

        // 2. Load and compress bitmap
        val bitmap: Bitmap? = try {
            if (bitmapDirect != null) {
                bitmapDirect
            } else if (imageUri != null) {
                val input: InputStream? = context.contentResolver.openInputStream(imageUri)
                BitmapFactory.decodeStream(input)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image", e)
            null
        }

        if (bitmap == null) {
            return@withContext GeminiAnalysisState.Error(
                message = "Could not load or decode the selected photo. Please try choosing another image.",
                canRetry = true
            )
        }

        // Scale bitmap down if larger than 1024x1024 to save bandwidth and fit payload limits
        val scaledBitmap = scaleBitmapIfNeeded(bitmap, 1024)
        val stream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        val imageBytes = stream.toByteArray()
        val base64Data = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

        // 3. Build structured prompt
        val systemInstruction = """
            You are AgriCare, a cautious plant-health assistant for Bangladeshi farmers, gardeners, and growers.
            Analyze only the visible evidence in the provided crop/leaf image.
            Do not claim certainty from one image. If evidence is weak or blurry, state uncertainty and ask for targeted context.
            Provide likely issue, visible symptoms, plausible alternative causes, low-risk immediate actions, prevention, and escalation advice.
            Do not invent product brands or illegal chemical recommendations. Always prefer low-risk cultural/mechanical controls first.
            Return strict JSON matching the requested schema.
            Language: ${if (languageCode == "bn") "Bangla (bn-BD)" else "English (en)"}.
        """.trimIndent()

        val promptText = buildString {
            append("Please inspect this crop/plant leaf image carefully. ")
            if (!userCropHint.isNullOrBlank()) {
                append("The user notes this plant is: $userCropHint. ")
            }
            append("Diagnose potential disease, pest, nutrient deficiency, or stress symptoms.")
        }

        val requestJson = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemInstruction) })
                })
            })
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", promptText) })
                        put(JSONObject().apply {
                            put("inlineData", JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64Data)
                            })
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.2)
            })
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$PRIMARY_MODEL:generateContent?key=$key"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            val response = client.newCall(request).execute()
            val code = response.code
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error ($code): $responseBody")
                return@withContext when (code) {
                    429 -> GeminiAnalysisState.Error(
                        message = "Gemini API free-tier quota exceeded. Please wait a few minutes before trying again, or consult the offline Crop Library.",
                        canRetry = true,
                        isQuotaExceeded = true
                    )
                    403, 400 -> GeminiAnalysisState.Error(
                        message = "API request rejected ($code). Please check that your Gemini API key has vision model permissions enabled.",
                        canRetry = false,
                        isApiKeyMissing = true
                    )
                    else -> GeminiAnalysisState.Error(
                        message = "Diagnosis request failed (HTTP $code). Please check network connection and try again.",
                        canRetry = true
                    )
                }
            }

            // Parse response
            val parsedResult = parseGeminiResponse(responseBody, imageUri?.toString(), languageCode)
            if (parsedResult != null) {
                GeminiAnalysisState.Success(parsedResult)
            } else {
                GeminiAnalysisState.Error(
                    message = "Could not parse diagnosis findings from model. Please retry or choose a clearer photo.",
                    canRetry = true
                )
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "No internet connection", e)
            GeminiAnalysisState.Error(
                message = "No internet connection detected. Online image diagnosis requires network access. You can consult the offline Crop & Disease Library.",
                canRetry = true,
                isOffline = true
            )
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Request timed out", e)
            GeminiAnalysisState.Error(
                message = "Image analysis timed out. The server took too long to respond. Please try again with a lighter photo.",
                canRetry = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error calling Gemini API", e)
            GeminiAnalysisState.Error(
                message = e.localizedMessage ?: "An unexpected error occurred during analysis.",
                canRetry = true
            )
        }
    }

    private fun parseGeminiResponse(
        jsonString: String,
        imageUriStr: String?,
        languageCode: String
    ): DiagnosisResult? {
        return try {
            val root = JSONObject(jsonString)
            val candidates = root.getJSONArray("candidates")
            if (candidates.length() == 0) return null
            val content = candidates.getJSONObject(0).getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val rawText = parts.getJSONObject(0).getString("text")

            val cleaned = cleanJsonString(rawText)
            val obj = JSONObject(cleaned)

            val cropGuess = obj.optString("crop_guess", "Crop / Plant")
            val issueTitle = obj.optString("issue_title", "Plant Health Issue")
            val scientificName = obj.optString("scientific_name", "")
            val assessmentLevel = obj.optString("assessment_level", "likely")
            val confidence = obj.optInt("confidence_percent", 88)
            val description = obj.optString("description", "")
            val category = obj.optString("category", "Fungal Disease")
            val escalationAdvice = obj.optString(
                "escalation_advice",
                "If symptoms spread across multiple rows, take a leaf sample to your local Upazila Agriculture Office."
            )
            val limitations = obj.optString(
                "limitations",
                "Based solely on visual foliage scan. Not a substitute for laboratory pathogen culture."
            )

            val symptoms = parseStringArray(obj.optJSONArray("visible_symptoms"))
            val alternatives = parseStringArray(obj.optJSONArray("alternative_causes"))
            val prevention = parseStringArray(obj.optJSONArray("prevention_steps"))
            val followUps = parseStringArray(obj.optJSONArray("follow_up_questions"))
            val safety = parseStringArray(obj.optJSONArray("safety_notes"))

            val immediateSteps = mutableListOf<TreatmentStepItem>()
            val stepsArray = obj.optJSONArray("immediate_steps")
            if (stepsArray != null) {
                for (i in 0 until stepsArray.length()) {
                    val stepObj = stepsArray.optJSONObject(i)
                    if (stepObj != null) {
                        immediateSteps.add(
                            TreatmentStepItem(
                                id = UUID.randomUUID().toString(),
                                orderIndex = i + 1,
                                title = stepObj.optString("title", "Step ${i + 1}"),
                                description = stepObj.optString("description", ""),
                                priority = stepObj.optString("priority", "now"),
                                isCompleted = false
                            )
                        )
                    } else {
                        val stepStr = stepsArray.optString(i)
                        if (stepStr.isNotBlank()) {
                            immediateSteps.add(
                                TreatmentStepItem(
                                    id = UUID.randomUUID().toString(),
                                    orderIndex = i + 1,
                                    title = stepStr,
                                    description = "",
                                    priority = "now",
                                    isCompleted = false
                                )
                            )
                        }
                    }
                }
            }

            DiagnosisResult(
                id = UUID.randomUUID().toString(),
                cropGuess = cropGuess,
                issueTitle = issueTitle,
                scientificName = scientificName,
                assessmentLevel = assessmentLevel,
                confidencePercent = confidence,
                description = description,
                category = category,
                visibleSymptoms = symptoms,
                alternativeCauses = alternatives,
                immediateSteps = immediateSteps,
                preventionSteps = prevention,
                followUpQuestions = followUps,
                escalationAdvice = escalationAdvice,
                safetyNotes = safety,
                limitations = limitations,
                language = languageCode,
                timestampMillis = System.currentTimeMillis(),
                imageUri = imageUriStr,
                imageRes = null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Gemini output JSON", e)
            null
        }
    }

    private fun cleanJsonString(raw: String): String {
        var str = raw.trim()
        if (str.startsWith("```json")) {
            str = str.removePrefix("```json")
        } else if (str.startsWith("```")) {
            str = str.removePrefix("```")
        }
        if (str.endsWith("```")) {
            str = str.removeSuffix("```")
        }
        return str.trim()
    }

    private fun parseStringArray(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            list.add(arr.optString(i))
        }
        return list
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDim: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDim && height <= maxDim) return bitmap
        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (ratio > 1) {
            newWidth = maxDim
            newHeight = (maxDim / ratio).toInt()
        } else {
            newHeight = maxDim
            newWidth = (maxDim * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
