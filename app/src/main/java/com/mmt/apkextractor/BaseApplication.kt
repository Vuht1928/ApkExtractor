package com.mmt.apkextractor

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.database.CursorWindow
import com.mmt.apkextractor.data.preferences.PreferenceRepository
import com.mmt.apkextractor.utils.SettingsManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {
    @Inject
    lateinit var prefs : PreferenceRepository

    override fun onCreate() {
        super.onCreate()
        runBlocking {
            //Setting UI Mode
            SettingsManager.changeUIMode(prefs.nightMode.first())
        }

        // TODO: Cần phải sửa lại id của notification channel
        // Create Notification Channel
        val channel = NotificationChannel(
            "AutoBackupService.CHANNEL_ID",
            "App Update Watching Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        // Open Channel with Notification Service
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Increase the CursorWindow size to 100 MB
        try {
            val field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field[null] = 100 * 1024 * 1024
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}