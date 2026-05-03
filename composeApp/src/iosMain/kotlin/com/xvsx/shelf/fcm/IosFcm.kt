package com.xvsx.shelf.fcm

import com.xvsx.shelf.push.FcmPushCoordinator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private object IosFcmKoin : KoinComponent {
    val coordinator: FcmPushCoordinator by inject()
}

fun notifyIosFcmToken(token: String) {
    IosFcmKoin.coordinator.notifyTokenReceived(token)
}
