package com.example.agricare.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AgriCareDao {
    // Crops
    @Query("SELECT * FROM plant_crops WHERE isArchived = 0 ORDER BY createdAtMillis DESC")
    fun getAllCrops(): Flow<List<PlantCropEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrop(crop: PlantCropEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrops(crops: List<PlantCropEntity>)

    @Update
    suspend fun updateCrop(crop: PlantCropEntity)

    @Delete
    suspend fun deleteCrop(crop: PlantCropEntity)

    // Diagnoses
    @Query("SELECT * FROM diagnosis_records ORDER BY timestampMillis DESC")
    fun getAllDiagnoses(): Flow<List<DiagnosisRecordEntity>>

    @Query("SELECT * FROM diagnosis_records WHERE id = :id LIMIT 1")
    suspend fun getDiagnosisById(id: String): DiagnosisRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(record: DiagnosisRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnoses(records: List<DiagnosisRecordEntity>)

    @Query("DELETE FROM diagnosis_records WHERE id = :id")
    suspend fun deleteDiagnosisById(id: String)

    @Query("DELETE FROM diagnosis_records")
    suspend fun clearAllDiagnoses()

    // Tasks
    @Query("SELECT * FROM crop_tasks ORDER BY isCompleted ASC, createdAtMillis ASC")
    fun getAllTasks(): Flow<List<CropTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: CropTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<CropTaskEntity>)

    @Update
    suspend fun updateTask(task: CropTaskEntity)

    @Delete
    suspend fun deleteTask(task: CropTaskEntity)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestampMillis DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notif: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifs: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // Clear all tables
    @Query("DELETE FROM plant_crops")
    suspend fun clearAllCrops()

    @Query("DELETE FROM crop_tasks")
    suspend fun clearAllTasks()
}
