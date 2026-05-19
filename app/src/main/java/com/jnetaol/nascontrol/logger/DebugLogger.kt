package com.jnetaol.nascontrol.logger

import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DebugLogger {
    private const val TAG = "NASControl"
    private const val LOG_FILE = "nascontrol_debug.log"
    var isDebugEnabled = true
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private var logFile: File? = null

    fun init(logDir: File) {
        logFile = File(logDir, LOG_FILE)
    }

    private fun log(level: String, code: String, message: String, throwable: Throwable? = null) {
        if (!isDebugEnabled && level != "E") return
        val timestamp = dateFormat.format(Date())
        val threadName = Thread.currentThread().name
        val fullMessage = "[$timestamp] [$level] [$code] [$threadName] $message"
        Log.println(
            when (level) {
                "D" -> Log.DEBUG
                "I" -> Log.INFO
                "W" -> Log.WARN
                "E" -> Log.ERROR
                else -> Log.VERBOSE
            },
            TAG, "[$code] $message"
        )
        try {
            logFile?.let { file ->
                if (!file.exists() || file.length() < 5 * 1024 * 1024) {
                    FileWriter(file, true).use { fw ->
                        PrintWriter(fw).use { pw ->
                            pw.println(fullMessage)
                            throwable?.printStackTrace(pw)
                        }
                    }
                }
            }
        } catch (_: Exception) { }
    }

    fun v(code: String, message: String) = log("V", code, message)
    fun d(code: String, message: String) = log("D", code, message)
    fun i(code: String, message: String) = log("I", code, message)
    fun w(code: String, message: String) = log("W", code, message)
    fun e(code: String, message: String, throwable: Throwable? = null) = log("E", code, message, throwable)

    fun getLogContent(): String {
        return try {
            logFile?.readText() ?: "No log file"
        } catch (_: Exception) {
            "Error reading log"
        }
    }

    fun clearLog() {
        try {
            logFile?.writeText("")
        } catch (_: Exception) { }
    }
}
