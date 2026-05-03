package com.xvsx.shelf.push

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private object IosFcmKoin : KoinComponent {
    val coordinator: FcmPushCoordinator by inject()
}

fun notifyIosFcmToken(token: String) {
    IosFcmKoin.coordinator.notifyTokenReceived(token)
}
