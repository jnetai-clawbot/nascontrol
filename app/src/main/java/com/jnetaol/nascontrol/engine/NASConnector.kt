package com.jnetaol.nascontrol.engine

import com.jnetaol.nascontrol.data.model.*
import com.jnetaol.nascontrol.logger.DebugLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class NASConnector {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun connect(config: ServerConfig): ConnectionResult = withContext(Dispatchers.IO) {
        try {
            DebugLogger.i("NC-010", "Connecting to ${config.host}:${config.port}")
            if (config.connectionType == "HTTP_API") {
                connectHttpApi(config)
            } else {
                simulateSshConnect(config)
            }
        } catch (e: Exception) {
            DebugLogger.e("NC-011", "Connection failed: ${e.message}", e)
            ConnectionResult(false, "Connection failed: ${e.message}")
        }
    }

    private suspend fun connectHttpApi(config: ServerConfig): ConnectionResult {
        val url = "http://${config.host}:${config.port}/api/status"
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            DebugLogger.i("NC-012", "HTTP API connection successful")
            return ConnectionResult(
                success = true,
                message = "Connected to ${config.name}",
                systemStats = simulateSystemStats(),
                disks = simulateDisks(config.id),
                containers = simulateContainers(config.id)
            )
        } else {
            return ConnectionResult(false, "HTTP ${response.code}: ${response.message}")
        }
    }

    private suspend fun simulateSshConnect(config: ServerConfig): ConnectionResult {
        DebugLogger.i("NC-013", "SSH simulation connecting to ${config.name}")
        kotlinx.coroutines.delay(1500)
        return ConnectionResult(
            success = true,
            message = "Connected to ${config.name} via SSH",
            systemStats = simulateSystemStats(),
            disks = simulateDisks(config.id),
            containers = simulateContainers(config.id)
        )
    }

    suspend fun fetchSystemStats(config: ServerConfig): SystemStats = withContext(Dispatchers.IO) {
        DebugLogger.d("NC-020", "Fetching system stats")
        kotlinx.coroutines.delay(500)
        simulateSystemStats()
    }

    suspend fun fetchDisks(config: ServerConfig): List<DiskInfo> = withContext(Dispatchers.IO) {
        DebugLogger.d("NC-021", "Fetching disk info")
        kotlinx.coroutines.delay(300)
        simulateDisks(config.id)
    }

    suspend fun fetchContainers(config: ServerConfig): List<DockerContainer> = withContext(Dispatchers.IO) {
        DebugLogger.d("NC-022", "Fetching Docker containers")
        kotlinx.coroutines.delay(400)
        simulateContainers(config.id)
    }

    suspend fun fetchFileList(config: ServerConfig, path: String): List<FileItem> = withContext(Dispatchers.IO) {
        DebugLogger.d("NC-023", "Fetching file list: $path")
        kotlinx.coroutines.delay(300)
        simulateFileList(path)
    }

    suspend fun containerAction(config: ServerConfig, containerId: String, action: String): Boolean =
        withContext(Dispatchers.IO) {
            DebugLogger.i("NC-024", "Container $action: $containerId")
            kotlinx.coroutines.delay(800)
            true
        }

    private fun simulateSystemStats(): SystemStats {
        val usedRam = (3.0 + Math.random() * 5).toFloat()
        val totalRam = 8f
        return SystemStats(
            cpuPercent = (15.0 + Math.random() * 60).toFloat(),
            cpuCores = 4,
            cpuTemp = (40.0 + Math.random() * 30).toFloat(),
            ramTotalGb = totalRam,
            ramUsedGb = usedRam,
            ramPercent = (usedRam / totalRam * 100),
            networkRxSpeed = "${(Math.random() * 50).toInt()} MB/s",
            networkTxSpeed = "${(Math.random() * 20).toInt()} MB/s",
            uptime = "${(1 + Math.random() * 30).toInt()}d ${(Math.random() * 24).toInt()}h",
            loadAverage = String.format("%.2f, %.2f, %.2f", Math.random() * 3, Math.random() * 2, Math.random()),
            osVersion = "Linux NAS 6.1.0",
            kernelVersion = "6.1.0-17-amd64"
        )
    }

    private fun simulateDisks(serverId: Long): List<DiskInfo> = listOf(
        DiskInfo(
            devicePath = "/dev/sda1",
            serverId = serverId,
            diskName = "System SSD",
            totalBytes = 256_000_000_000L,
            usedBytes = 89_000_000_000L,
            freeBytes = 167_000_000_000L,
            healthStatus = "Good",
            temperature = 38,
            filesystem = "ext4",
            mountPoint = "/"
        ),
        DiskInfo(
            devicePath = "/dev/sdb1",
            serverId = serverId,
            diskName = "Storage HDD",
            totalBytes = 4_000_000_000_000L,
            usedBytes = 2_800_000_000_000L,
            freeBytes = 1_200_000_000_000L,
            healthStatus = "Warning",
            temperature = 45,
            filesystem = "ext4",
            mountPoint = "/mnt/storage"
        ),
        DiskInfo(
            devicePath = "/dev/sdc1",
            serverId = serverId,
            diskName = "Media Drive",
            totalBytes = 2_000_000_000_000L,
            usedBytes = 1_500_000_000_000L,
            freeBytes = 500_000_000_000L,
            healthStatus = "Good",
            temperature = 42,
            filesystem = "ext4",
            mountPoint = "/mnt/media"
        )
    )

    private fun simulateContainers(serverId: Long): List<DockerContainer> = listOf(
        DockerContainer(
            containerId = "abc123def",
            serverId = serverId,
            name = "plex",
            image = "plexinc/pms-docker:latest",
            status = "running",
            uptime = "5d 12h",
            cpuPercent = 8.5f,
            memUsage = "1.2 GB",
            ports = "32400:32400"
        ),
        DockerContainer(
            containerId = "ghi456jkl",
            serverId = serverId,
            name = "sonarr",
            image = "linuxserver/sonarr:latest",
            status = "running",
            uptime = "12d 3h",
            cpuPercent = 2.1f,
            memUsage = "512 MB",
            ports = "8989:8989"
        ),
        DockerContainer(
            containerId = "mno789pqr",
            serverId = serverId,
            name = "radarr",
            image = "linuxserver/radarr:latest",
            status = "running",
            uptime = "12d 3h",
            cpuPercent = 1.8f,
            memUsage = "480 MB",
            ports = "7878:7878"
        ),
        DockerContainer(
            containerId = "stu012vwx",
            serverId = serverId,
            name = "nextcloud",
            image = "nextcloud:latest",
            status = "stopped",
            uptime = "",
            cpuPercent = 0f,
            memUsage = "0 MB",
            ports = "8080:80"
        ),
        DockerContainer(
            containerId = "yzab34cde",
            serverId = serverId,
            name = "portainer",
            image = "portainer/portainer-ce:latest",
            status = "running",
            uptime = "30d 1h",
            cpuPercent = 1.2f,
            memUsage = "256 MB",
            ports = "9443:9443,8000:8000"
        )
    )

    private fun simulateFileList(path: String): List<FileItem> {
        val basePath = if (path == "/") "/mnt/storage" else path
        return listOf(
            FileItem("Documents", "$basePath/Documents", true, 0L, System.currentTimeMillis(), "drwxr-xr-x", "admin"),
            FileItem("Media", "$basePath/Media", true, 0L, System.currentTimeMillis(), "drwxr-xr-x", "admin"),
            FileItem("Backups", "$basePath/Backups", true, 0L, System.currentTimeMillis(), "drwxr-xr-x", "admin"),
            FileItem("Downloads", "$basePath/Downloads", true, 0L, System.currentTimeMillis(), "drwxr-xr-x", "admin"),
            FileItem("docker-compose.yml", "$basePath/docker-compose.yml", false, 4096, System.currentTimeMillis(), "-rw-r--r--", "admin"),
            FileItem("README.txt", "$basePath/README.txt", false, 2048, System.currentTimeMillis(), "-rw-r--r--", "admin"),
            FileItem("backup_2026-05-19.tar.gz", "$basePath/backup_2026-05-19.tar.gz", false, 524_288_000, System.currentTimeMillis(), "-rw-r--r--", "admin"),
            FileItem("system.log", "$basePath/system.log", false, 1_048_576, System.currentTimeMillis(), "-rw-r-----", "root")
        )
    }
}
