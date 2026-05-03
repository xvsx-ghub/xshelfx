package com.xvsx.shelf.push

import com.xvsx.shelf.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FcmPushCoordinator(
    private val pushTokenRegistrar: PushTokenRegistrar,
    private val appBadgeService: AppBadgeService,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _inboundMessages = MutableSharedFlow<FcmInboundMessage>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val inboundMessages: SharedFlow<FcmInboundMessage> = _inboundMessages.asSharedFlow()

    companion object {
        private const val TAG = "FcmPushCoordinator"
    }

    fun notifyInboundMessage(message: FcmInboundMessage) {
        scope.launch {
            Logger.d(TAG, "New notification. " +
                    "Title: ${message.notificationTitle}" +
                    " Body: ${message.notificationBody}")
            _inboundMessages.emit(message)
            appBadgeService.recordInboundPushForLauncherBadge(message)
        }
    }

    fun notifyTokenReceived(token: String) {
        if (token.isBlank()) {
            Logger.d(TAG, "Ignored blank FCM token")
            return
        }
        scope.launch {
            pushTokenRegistrar.onNewToken(token)
        }
    }
}