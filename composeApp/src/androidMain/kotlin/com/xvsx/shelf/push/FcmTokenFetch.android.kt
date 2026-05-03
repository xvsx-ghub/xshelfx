package com.xvsx.shelf.push

import com.google.firebase.messaging.FirebaseMessaging
import com.xvsx.shelf.util.Logger

private const val TAG = "FcmCurrentToken"

actual fun fetchCurrentPushTokenForRegistration(onResult: (Result<String>) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            val ex = task.exception ?: Exception("getToken failed")
            Logger.e(TAG, "getToken failed: ${ex.message}", ex)
            onResult(Result.failure(ex))
            return@addOnCompleteListener
        }
        val token = task.result
        if (token.isNullOrBlank()) {
            onResult(Result.failure(IllegalStateException("FCM token was null or blank")))
            return@addOnCompleteListener
        }
        Logger.d(TAG, "Current FCM token: $token")
        onResult(Result.success(token))
    }
}
