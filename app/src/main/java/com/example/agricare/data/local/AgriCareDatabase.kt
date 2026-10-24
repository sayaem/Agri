package com.example.agricare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlantCropEntity::class,
        DiagnosisRecordEntity::class,
        CropTaskEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AgriCareDatabase : RoomDatabase() {
    abstract fun dao(): AgriCareDao

    companion object {
        @Volatile
        private var INSTANCE: AgriCareDatabase? = null

        fun getDatabase(context: Context): AgriCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgriCareDatabase::class.java,
                    "agricare_master.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
