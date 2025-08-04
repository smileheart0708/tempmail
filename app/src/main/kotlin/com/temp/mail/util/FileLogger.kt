package com.temp.mail.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLogger(private val context: Context) {

    private val logFile: File by lazy {
        val logDir = context.externalCacheDir ?: context.cacheDir
        File(logDir, "error.log")
    }

    /**
     * Asynchronously appends an error log to the app's external cache directory.
     * This is safe to call from the main thread.
     *
     * @param tag A tag for the log message, similar to Logcat.
     * @param message A descriptive message for the error.
     * @param throwable The exception that was thrown.
     */
    suspend fun logError(tag: String, message: String, throwable: Throwable) {
        withContext(Dispatchers.IO) {
            try {
                // Create a detailed, multi-line log entry.
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
                val logContent = buildString {
                    append("[$timestamp] E/$tag: $message\n")
                    append("Exception: ${throwable.javaClass.simpleName}\n")
                    append("Message: ${throwable.message}\n")
                    append("Stacktrace:\n${throwable.stackTraceToString()}\n\n")
                }

                logFile.appendText(logContent)

            } catch (e: IOException) {
                // If writing to file fails, print to Logcat as a last resort.
                e.printStackTrace()
            }
        }
    }
}