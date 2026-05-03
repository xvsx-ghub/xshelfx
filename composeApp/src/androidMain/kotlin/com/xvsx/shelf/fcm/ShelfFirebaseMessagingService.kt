package com.xvsx.shelf.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.xvsx.shelf.R
import com.xvsx.shelf.push.FcmPushCoordinator
import com.xvsx.shelf.push.parseFcmDisplayMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShelfFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val fcmPushCoordinator: FcmPushCoordinator by inject()

    override fun onNewToken(token: String) {
        fcmPushCoordinator.notifyTokenReceived(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val display = parseFcmDisplayMessage(
            defaultTitle = getString(R.string.app_name),
            notificationTitle = message.notification?.title,
            notificationBody = message.notification?.body,
            data = message.data,
        ) ?: return

        ensureChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(display.title)
            .setContentText(display.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(message.messageId?.hashCode() ?: 0, notification)
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.fcm_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "shelf_fcm_default"
    }
}
