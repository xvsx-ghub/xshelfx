package com.xvsx.shelf.chat

import androidx.compose.runtime.Composable

interface MediaPickController {
    fun pickImage()

    fun pickVideo()

    fun pickAudio()
}

@Composable
expect fun rememberMediaPickController(
    onPicked: (bytes: ByteArray, mimeType: String, displayName: String?) -> Unit,
): MediaPickController
