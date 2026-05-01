package com.xvsx.shelf.push

import com.xvsx.shelf.data.local.RepositoryLocal
import com.xvsx.shelf.data.remote.http.Http
import com.xvsx.shelf.data.remote.http.HttpClientCore.HttpStatus
import com.xvsx.shelf.util.Logger

class PushTokenRegistrar(
    private val http: Http,
    private val repositoryLocal: RepositoryLocal,
) {
    companion object {
        private const val TAG = "PushTokenRegistrar"
    }

    suspend fun onNewToken(token: String) {
        Logger.d(TAG, "FCM token: $token")
        if (repositoryLocal.getSessionKey().isBlank()) {
            repositoryLocal.setPendingPushToken(token)
            Logger.d(TAG, "FCM token stored until session is available")
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
        http.registerPushToken(
            pushToken = token,
            platform = pushTokenPlatform(),
            onEvent = { status, _, error ->
                when (status) {
                    HttpStatus.Completed -> {
                        if (error != null) {
                            Logger.e(TAG, "RegisterPushToken failed: ${error.message}", error)
                        } else {
                            repositoryLocal.clearPendingPushToken()
                        }
                    }

                    else -> {}
                }
            }
        )
    }
}
