package com.xvsx.shelf.chat

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
actual fun InlineAudioPlayer(absolutePath: String, mimeType: String) {
    TextButton(onClick = { openMediaExternally(absolutePath, mimeType) }) {
        Text("Play")
    }
}

actual fun formatTime(timestampMillis: Long): String {
    val totalMin = ((timestampMillis / 60000) % (24 * 60)).toInt()
    val h = totalMin / 60
    val m = totalMin % 60
    val hs = h.toString().padStart(2, '0')
    val ms = m.toString().padStart(2, '0')
    return "$hs:$ms"
}
