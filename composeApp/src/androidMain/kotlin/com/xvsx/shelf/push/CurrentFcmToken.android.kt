package com.xvsx.shelf.push

import com.google.firebase.messaging.FirebaseMessaging
import com.xvsx.shelf.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val androidFcmScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

private object AndroidFcmKoin : KoinComponent {
    val registrar: PushTokenRegistrar by inject()
}

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
        val token = task.result ?: return@addOnCompleteListener
        Logger.d("FcmCurrentToken", "Current FCM token: $token")
        androidFcmScope.launch {
            AndroidFcmKoin.registrar.onNewToken(token)
        }
    }
}
