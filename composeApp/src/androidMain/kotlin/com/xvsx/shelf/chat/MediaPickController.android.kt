package com.xvsx.shelf.chat

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberMediaPickController(
    onPicked: (bytes: ByteArray, mimeType: String, displayName: String?) -> Unit,
): MediaPickController {
    val context = LocalContext.current
    val resolver = context.contentResolver
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return@launch
            val mime = resolver.getType(uri) ?: "application/octet-stream"
            val name = runCatching {
                resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                    ?.use { cursor ->
                        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (idx >= 0 && cursor.moveToFirst()) cursor.getString(idx) else null
                    }
            }.getOrNull()
            withContext(Dispatchers.Main) {
                onPicked(bytes, mime, name)
            }
        }
    }
    return remember(launcher) {
        object : MediaPickController {
            override fun pickImage() {
                launcher.launch("image/*")
            }

            override fun pickVideo() {
                launcher.launch("video/*")
            }

            override fun pickAudio() {
                launcher.launch("audio/*")
            }
        }
    }
}
