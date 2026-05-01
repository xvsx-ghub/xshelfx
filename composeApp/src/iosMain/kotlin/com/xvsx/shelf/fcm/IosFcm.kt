package com.xvsx.shelf.fcm

import com.xvsx.shelf.push.PushTokenRegistrar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val iosFcmScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

private object IosFcmKoin : KoinComponent {
    val registrar: PushTokenRegistrar by inject()
}

fun notifyIosFcmToken(token: String) {
    iosFcmScope.launch {
        IosFcmKoin.registrar.onNewToken(token)
    }
}
