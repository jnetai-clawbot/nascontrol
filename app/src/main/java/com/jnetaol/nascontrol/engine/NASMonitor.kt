package com.jnetaol.nascontrol.engine

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jnetaol.nascontrol.MainActivity
import com.jnetaol.nascontrol.NASControlApp
import com.jnetaol.nascontrol.data.model.*
import com.jnetaol.nascontrol.logger.DebugLogger
import kotlinx.coroutines.*
import kotlin.random.Random

class NASMonitorService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val connector = NASConnector()
    private var monitorJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        DebugLogger.i("NC-030", "Monitor service created")
        startForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serverId = intent?.getLongExtra("server_id", -1L) ?: -1L
        if (serverId > 0) {
            startMonitoring(serverId)
        }
        return START_STICKY
    }

    private fun startForeground() {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, NASControlApp.CHANNEL_ALERTS)
            .setContentTitle("NASControl Monitor")
            .setContentText("Monitoring NAS server...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
    }

    private fun startMonitoring(serverId: Long) {
        monitorJob?.cancel()
        monitorJob = scope.launch {
            while (isActive) {
                try {
                    checkServerHealth(serverId)
                } catch (e: Exception) {
                    DebugLogger.e("NC-031", "Monitor error: ${e.message}", e)
                }
                delay(30_000L)
            }
        }
    }

    private suspend fun checkServerHealth(serverId: Long) {
        val db = (application as NASControlApp).database
        val config = db.serverConfigDao().getById(serverId) ?: return
        val stats = connector.fetchSystemStats(config)

        val alerts = mutableListOf<Alert>()

        if (stats.cpuPercent > 90f) {
            alerts.add(
                Alert(
                    id = "cpu-${System.currentTimeMillis()}",
                    title = "High CPU Usage",
                    message = "CPU at ${String.format("%.1f", stats.cpuPercent)}%",
                    severity = "Critical",
                    source = config.name
                )
            )
        } else if (stats.cpuPercent > 75f) {
            alerts.add(
                Alert(
                    id = "cpu-${System.currentTimeMillis()}",
                    title = "High CPU Usage",
                    message = "CPU at ${String.format("%.1f", stats.cpuPercent)}%",
                    severity = "Warning",
                    source = config.name
                )
            )
        }

        if (stats.ramPercent > 90f) {
            alerts.add(
                Alert(
                    id = "ram-${System.currentTimeMillis()}",
                    title = "High Memory Usage",
                    message = "RAM at ${String.format("%.1f", stats.ramPercent)}%",
                    severity = "Critical",
                    source = config.name
                )
            )
        } else if (stats.ramPercent > 80f) {
            alerts.add(
                Alert(
                    id = "ram-${System.currentTimeMillis()}",
                    title = "High Memory Usage",
                    message = "RAM at ${String.format("%.1f", stats.ramPercent)}%",
                    severity = "Warning",
                    source = config.name
                )
            )
        }

        val disks = connector.fetchDisks(config)
        for (disk in disks) {
            val usagePercent = if (disk.totalBytes > 0) {
                (disk.usedBytes.toFloat() / disk.totalBytes.toFloat() * 100)
            } else 0f
            if (usagePercent > 95f) {
                alerts.add(
                    Alert(
                        id = "disk-${disk.devicePath}-${System.currentTimeMillis()}",
                        title = "Disk Almost Full",
                        message = "${disk.diskName}: ${String.format("%.1f", usagePercent)}% used",
                        severity = "Critical",
                        source = config.name
                    )
                )
            } else if (usagePercent > 85f) {
                alerts.add(
                    Alert(
                        id = "disk-${disk.devicePath}-${System.currentTimeMillis()}",
                        title = "Disk Usage Warning",
                        message = "${disk.diskName}: ${String.format("%.1f", usagePercent)}% used",
                        severity = "Warning",
                        source = config.name
                    )
                )
            }
        }

        for (alert in alerts) {
            showAlertNotification(alert)
        }
        DebugLogger.d("NC-032", "Health check complete: ${alerts.size} alerts")
    }

    private fun showAlertNotification(alert: Alert) {
        val pendingIntent = PendingIntent.getActivity(
            this, alert.id.hashCode(),
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, NASControlApp.CHANNEL_ALERTS)
            .setContentTitle("[${alert.severity}] ${alert.title}")
            .setContentText(alert.message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val nm = getSystemService(android.app.NotificationManager::class.java)
        nm.notify(alert.id.hashCode(), notification)
    }

    override fun onDestroy() {
        monitorJob?.cancel()
        scope.cancel()
        DebugLogger.i("NC-033", "Monitor service destroyed")
        super.onDestroy()
    }
}
