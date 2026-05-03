package com.xvsx.shelf.push

/**
 * Normalized FCM payload delivered from Android [com.google.firebase.messaging.RemoteMessage]
 * or iOS notification / remote-notification userInfo.
 */
data class FcmInboundMessage(
    val messageId: String?,
    val collapseKey: String?,
    val data: Map<String, String>,
    val notificationTitle: String?,
    val notificationBody: String?,
) {
    fun toDisplayMessageOrNull(defaultTitle: String): FcmDisplayMessage? = parseFcmDisplayMessage(
        defaultTitle = defaultTitle,
        notificationTitle = notificationTitle,
        notificationBody = notificationBody,
        data = data,
    )
}
