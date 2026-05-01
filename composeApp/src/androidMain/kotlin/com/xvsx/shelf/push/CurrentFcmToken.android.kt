package com.xvsx.shelf.push

import com.google.firebase.messaging.FirebaseMessaging
import com.xvsx.shelf.util.Logger

actual fun logCurrentFcmToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Logger.e(
                "FcmCurrentToken",
                "getToken failed: ${task.exception?.message}",
                task.exception
            )
            return@addOnCompleteListener
        }
        Logger.d("FcmCurrentToken", "Current FCM token: ${task.result}")
    }
}
