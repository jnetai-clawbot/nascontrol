package com.jnetaol.nascontrol.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_configs")
data class ServerConfig(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val host: String,
    val port: Int = 22,
    val username: String = "root",
    val password: String = "",
    val connectionType: String = "SSH", // SSH, HTTP_API
    val isDefault: Boolean = false,
    val lastConnected: Long = 0L,
    val isConnected: Boolean = false
)

@Entity(tableName = "docker_containers")
data class DockerContainer(
    @PrimaryKey val containerId: String,
    val serverId: Long,
    val name: String,
    val image: String,
    val status: String, // running, stopped, paused
    val uptime: String = "",
    val cpuPercent: Float = 0f,
    val memUsage: String = "",
    val ports: String = ""
)

@Entity(tableName = "disk_infos")
data class DiskInfo(
    @PrimaryKey val devicePath: String,
    val serverId: Long,
    val diskName: String,
    val totalBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val freeBytes: Long = 0L,
    val healthStatus: String = "Good", // Good, Warning, Critical
    val temperature: Int = 0,
    val filesystem: String = "",
    val mountPoint: String = ""
)

@Entity(tableName = "backup_tasks")
data class BackupTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serverId: Long,
    val name: String,
    val sourcePath: String,
    val destPath: String,
    val schedule: String = "Manual", // Manual, Daily, Weekly, Monthly
    val lastRun: Long = 0L,
    val nextRun: Long = 0L,
    val status: String = "Idle", // Idle, Running, Completed, Failed
    val progress: Int = 0,
    val totalFiles: Int = 0,
    val processedFiles: Int = 0
)

data class SystemStats(
    val cpuPercent: Float = 0f,
    val cpuCores: Int = 4,
    val cpuTemp: Float = 0f,
    val ramTotalGb: Float = 0f,
    val ramUsedGb: Float = 0f,
    val ramPercent: Float = 0f,
    val networkRxBytes: Long = 0L,
    val networkTxBytes: Long = 0L,
    val networkRxSpeed: String = "0 KB/s",
    val networkTxSpeed: String = "0 KB/s",
    val uptime: String = "",
    val loadAverage: String = "",
    val osVersion: String = "",
    val kernelVersion: String = ""
)

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0L,
    val modifiedTime: Long = 0L,
    val permissions: String = "",
    val owner: String = ""
)

data class Alert(
    val id: String,
    val title: String,
    val message: String,
    val severity: String, // Info, Warning, Critical
    val timestamp: Long = System.currentTimeMillis(),
    val source: String = ""
)

data class ConnectionResult(
    val success: Boolean,
    val message: String,
    val systemStats: SystemStats? = null,
    val disks: List<DiskInfo> = emptyList(),
    val containers: List<DockerContainer> = emptyList()
)
