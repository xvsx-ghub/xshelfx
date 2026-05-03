package com.xvsx.shelf.push

import com.xvsx.shelf.util.Logger
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private object IosFcmKoin : KoinComponent {
    val coordinator: FcmPushCoordinator by inject()
}

private val iosFcmJson = Json { ignoreUnknownKeys = true }
private val iosFcmDataSerializer = MapSerializer(String.serializer(), String.serializer())

private const val TAG = "IosFcm"

fun notifyIosFcmToken(token: String) {
    IosFcmKoin.coordinator.notifyTokenReceived(token)
}

/**
 * Called from Swift when a push payload is received (foreground presentation, user tap, or
 * [UIApplicationDelegate application:didReceiveRemoteNotification:fetchCompletionHandler:]).
 *
 * @param dataJson JSON object of string key/value pairs derived from FCM `userInfo` (excluding `aps`).
 */
fun notifyIosFcmMessage(
    messageId: String?,
    collapseKey: String?,
    notificationTitle: String?,
    notificationBody: String?,
    dataJson: String,
) {
    val data = try {
        iosFcmJson.decodeFromString(iosFcmDataSerializer, dataJson)
    } catch (e: Throwable) {
        Logger.e(TAG, "Failed to parse FCM data JSON", e)
        emptyMap()
    }
    val message = FcmInboundMessage(
        messageId = messageId?.takeIf { it.isNotBlank() },
        collapseKey = collapseKey?.takeIf { it.isNotBlank() },
        data = data,
        notificationTitle = notificationTitle?.takeIf { it.isNotBlank() },
        notificationBody = notificationBody?.takeIf { it.isNotBlank() },
    )
    IosFcmKoin.coordinator.notifyInboundMessage(message)
}