package com.jnetaol.nascontrol

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.jnetaol.nascontrol.data.db.AppDatabase
import com.jnetaol.nascontrol.logger.DebugLogger

class NASControlApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        DebugLogger.i("NC-001", "NASControlApp initialized")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ALERTS,
                "NAS Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for NAS monitoring"
                enableVibration(true)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ALERTS = "nas_alerts"
        lateinit var instance: NASControlApp
            private set
    }
}
