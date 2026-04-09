package com.xvsx.shelf.chat

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

actual fun openMediaExternally(absolutePath: String, mimeType: String) {
    OpenMediaHost.open(absolutePath, mimeType)
}

internal object OpenMediaHost {
    private var opener: ((String, String) -> Unit)? = null

    fun register(opener: (String, String) -> Unit) {
        this.opener = opener
    }

    fun unregister() {
        opener = null
    }

    fun open(absolutePath: String, mimeType: String) {
        opener?.invoke(absolutePath, mimeType)
    }
}

@Composable
fun RegisterOpenMediaHandler() {
    val context = LocalContext.current
    DisposableEffect(context) {
        OpenMediaHost.register { path, mime ->
            val file = File(path)
            if (!file.exists()) return@register
            val extMime = mime.ifBlank {
                file.extension.takeIf { it.isNotEmpty() }
                    ?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }
            } ?: "*/*"
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, extMime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, null))
        }
        onDispose { OpenMediaHost.unregister() }
    }
}
