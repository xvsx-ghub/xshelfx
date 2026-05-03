package com.xvsx.shelf.push

import com.xvsx.shelf.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Single entry for FCM token delivery from any platform path (refresh, cold start, Messaging delegate).
 */
class FcmPushCoordinator(
    private val pushTokenRegistrar: PushTokenRegistrar,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    companion object {
        private const val TAG = "FcmPushCoordinator"
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
