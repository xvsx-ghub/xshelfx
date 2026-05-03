package com.xvsx.shelf.push

/**
 * iOS delivers the token from native Firebase Messaging; Kotlin does not query it here.
 */
class FcmKotlinTokenFetchSkipped : Exception(
    "FCM token on iOS is obtained via Firebase Messaging delegate, not from Kotlin fetch.",
)

/**
 * Requests the current registration token from the platform FCM SDK, if available from Kotlin.
 */
expect fun fetchCurrentPushTokenForRegistration(onResult: (Result<String>) -> Unit)
