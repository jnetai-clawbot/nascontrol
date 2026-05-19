package com.jnetaol.nascontrol.engine

import com.jnetaol.nascontrol.data.model.BackupTask
import com.jnetaol.nascontrol.logger.DebugLogger
import kotlinx.coroutines.*
import java.io.File

class BackupManager {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun startBackup(task: BackupTask, onProgress: (Int, Int) -> Unit): Boolean {
        DebugLogger.i("NC-040", "Starting backup: ${task.name}")
        val totalFiles = 100
        for (i in 1..totalFiles) {
            delay(100)
            onProgress(i, totalFiles)
            if (!scope.isActive) return false
        }
        DebugLogger.i("NC-041", "Backup complete: ${task.name}")
        return true
    }

    suspend fun simulateBackup(task: BackupTask): Boolean {
        DebugLogger.i("NC-042", "Simulating backup: ${task.name}")
        delay(2000)
        return Math.random() > 0.2
    }

    fun cancelBackup(taskId: Long) {
        DebugLogger.w("NC-043", "Backup cancelled: $taskId")
        scope.coroutineContext.cancel()
    }

    fun calculateNextRun(schedule: String): Long {
        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L
        return when (schedule) {
            "Daily" -> now + dayMillis
            "Weekly" -> now + dayMillis * 7
            "Monthly" -> now + dayMillis * 30
            else -> 0L
        }
    }

    fun getScheduleOptions(): List<String> = listOf("Manual", "Daily", "Weekly", "Monthly")
}
