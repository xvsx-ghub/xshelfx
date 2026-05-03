package com.xvsx.shelf.push

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.xvsx.shelf.R

private lateinit var appContext: Application

fun setAppBadgeAndroidContext(application: Application) {
    appContext = application.applicationContext as Application
}

private const val BADGE_CHANNEL_ID = "shelf_app_icon_badge"
private const val BADGE_NOTIFICATION_ID = 91_001

private fun ensureBadgeChannel() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = appContext.getSystemService(NotificationManager::class.java) ?: return
    if (manager.getNotificationChannel(BADGE_CHANNEL_ID) != null) return
    val channel = NotificationChannel(
        BADGE_CHANNEL_ID,
        appContext.getString(R.string.app_badge_notification_channel_name),
        NotificationManager.IMPORTANCE_LOW,
    ).apply {
        setShowBadge(true)
        enableVibration(false)
        setSound(null, null)
    }
    manager.createNotificationChannel(channel)
}

actual fun applyAppIconBadgeCount(count: Int) {
    if (!::appContext.isInitialized) return
    ensureBadgeChannel()
    val nm = NotificationManagerCompat.from(appContext)
    if (count <= 0) {
        nm.cancel(BADGE_NOTIFICATION_ID)
        return
    }
    val notification = NotificationCompat.Builder(appContext, BADGE_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(appContext.getString(R.string.app_badge_notification_title))
        .setContentText(appContext.getString(R.string.app_badge_notification_body, count))
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setNumber(count)
        .setOnlyAlertOnce(true)
        .setAutoCancel(false)
        .build()
    try {
        nm.notify(BADGE_NOTIFICATION_ID, notification)
    } catch (_: SecurityException) {
        // POST_NOTIFICATIONS not granted on API 33+.
    }
}