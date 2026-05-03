package com.xvsx.shelf.push

/**
 * Resolved title and body for a local notification, shared between Android and any other consumer.
 */
data class FcmDisplayMessage(
    val title: String,
    val body: String,
)

/**
 * Maps FCM notification + data payload to a displayable message (mirrors Android service rules).
 */
fun parseFcmDisplayMessage(
    defaultTitle: String,
    notificationTitle: String?,
    notificationBody: String?,
    data: Map<String, String>,
): FcmDisplayMessage? {
    val title = notificationTitle?.takeIf { it.isNotBlank() }
        ?: data["title"]?.takeIf { it.isNotBlank() }
        ?: defaultTitle
    val body = notificationBody?.takeIf { it.isNotBlank() }
        ?: data["body"]?.takeIf { it.isNotBlank() }
        ?: data["message"]?.takeIf { it.isNotBlank() }
        ?: return null
    return FcmDisplayMessage(title = title, body = body)
}
