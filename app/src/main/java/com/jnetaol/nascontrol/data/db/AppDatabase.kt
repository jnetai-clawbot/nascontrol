package com.jnetaol.nascontrol.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jnetaol.nascontrol.data.model.*

@Database(
    entities = [
        ServerConfig::class,
        DockerContainer::class,
        DiskInfo::class,
        BackupTask::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverConfigDao(): ServerConfigDao
    abstract fun dockerContainerDao(): DockerContainerDao
    abstract fun diskInfoDao(): DiskInfoDao
    abstract fun backupTaskDao(): BackupTaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nascontrol.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
