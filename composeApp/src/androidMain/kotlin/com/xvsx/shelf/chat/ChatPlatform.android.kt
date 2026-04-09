package com.xvsx.shelf.chat

import android.media.MediaPlayer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
actual fun InlineAudioPlayer(absolutePath: String, mimeType: String) {
    val player = remember(absolutePath) {
        MediaPlayer().apply {
            setDataSource(absolutePath)
            prepare()
        }
    }
    var playing by remember { mutableStateOf(false) }
    DisposableEffect(player) {
        onDispose {
            runCatching {
                if (player.isPlaying) player.stop()
                player.release()
            }
        }
    }
    TextButton(
        onClick = {
            runCatching {
                if (playing) {
                    player.pause()
                    playing = false
                } else {
                    player.start()
                    playing = true
                }
            }
        },
    ) {
        Text(if (playing) "Pause" else "Play")
    }
}

actual fun formatTime(timestampMillis: Long): String {
    val fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())
    return fmt.format(Date(timestampMillis))
}
