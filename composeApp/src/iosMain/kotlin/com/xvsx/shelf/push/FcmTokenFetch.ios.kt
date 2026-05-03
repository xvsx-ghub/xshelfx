package com.xvsx.shelf.push

actual fun fetchCurrentPushTokenForRegistration(onResult: (Result<String>) -> Unit) {
    onResult(Result.failure(FcmKotlinTokenFetchSkipped()))
}
