package com.xvsx.shelf.push

import com.xvsx.shelf.data.local.RepositoryLocal
import com.xvsx.shelf.data.remote.http.Http
import com.xvsx.shelf.data.remote.http.HttpClientCore.HttpStatus
import com.xvsx.shelf.util.Logger
import com.xvsx.shelf.util.System

class PushTokenRegistrar(
    private val http: Http,
    private val repositoryLocal: RepositoryLocal,
    private val system: System,
) {
    companion object {
        private const val TAG = "PushTokenRegistrar"
    }

    suspend fun onNewToken(token: String) {
        Logger.d(TAG, "FCM token: $token")
        if (repositoryLocal.getBaseUrl().isBlank()) {
            repositoryLocal.setPendingPushToken(token)
            Logger.d(TAG, "FCM token stored until base URL is configured")
            return
        }
        registerAndClearPendingOnSuccess(token)
    }

    suspend fun flushPendingAfterLogin() {
        val pending = repositoryLocal.getPendingPushToken() ?: return
        Logger.d(TAG, "FCM token (after login): $pending")
        registerAndClearPendingOnSuccess(pending)
    }

    private suspend fun registerAndClearPendingOnSuccess(token: String) {
        val deviceId = system.getDeviceInfo().id
        http.registerPushToken(
            pushToken = token,
            deviceId = deviceId,
            onEvent = { status, success, error ->
                when (status) {
                    HttpStatus.Completed -> {
                        if (success) {
                            repositoryLocal.clearPendingPushToken()
                        } else if (error != null) {
                            Logger.e(TAG, "FCM register failed: ${error.message}", error)
                        }
                    }

                    else -> {}
                }
            }
        )
    }
}
