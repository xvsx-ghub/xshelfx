package com.xvsx.shelf.push

import com.google.firebase.messaging.RemoteMessage

internal fun RemoteMessage.toFcmInboundMessage(): FcmInboundMessage = FcmInboundMessage(
    messageId = messageId,
    collapseKey = collapseKey,
    data = data,
    notificationTitle = notification?.title,
    notificationBody = notification?.body,
)