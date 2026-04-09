package com.xvsx.shelf.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypeAudio
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.UniformTypeIdentifiers.UTTypeMovie
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberMediaPickController(
    onPicked: (bytes: ByteArray, mimeType: String, displayName: String?) -> Unit,
): MediaPickController {
    val handler = rememberUpdatedState(onPicked)
    return remember {
        object : MediaPickController {
            override fun pickImage() {
                IosDocumentPicker.present(listOf(UTTypeImage)) { bytes, mime, name ->
                    handler.value(bytes, mime, name)
                }
            }

            override fun pickVideo() {
                IosDocumentPicker.present(listOf(UTTypeMovie)) { bytes, mime, name ->
                    handler.value(bytes, mime, name)
                }
            }

            override fun pickAudio() {
                IosDocumentPicker.present(listOf(UTTypeAudio)) { bytes, mime, name ->
                    handler.value(bytes, mime, name)
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private object IosDocumentPicker {

    private var retainedDelegate: DocumentDelegate? = null

    fun present(
        types: List<*>,
        onResult: (ByteArray, String, String?) -> Unit,
    ) {
        val root = rootViewController() ?: return
        val delegate = DocumentDelegate(
            onResult = { bytes, mime, name ->
                retainedDelegate = null
                onResult(bytes, mime, name)
            },
            onCancel = {
                retainedDelegate = null
            },
        )
        retainedDelegate = delegate
        val picker = UIDocumentPickerViewController(forOpeningContentTypes = types, asCopy = true)
        picker.delegate = delegate
        root.presentViewController(picker, animated = true, completion = null)
    }

    @Suppress("DEPRECATION")
    private fun rootViewController(): UIViewController? {
        val app = UIApplication.sharedApplication
        return app.keyWindow?.rootViewController
    }
}

@OptIn(ExperimentalForeignApi::class)
private class DocumentDelegate(
    private val onResult: (ByteArray, String, String?) -> Unit,
    private val onCancel: () -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>,
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: run {
            onCancel()
            return
        }
        val accessing = url.startAccessingSecurityScopedResource()
        try {
            val path = url.path ?: run {
                onCancel()
                return
            }
            val bytes = readFileBytesPosixOrNull(path) ?: run {
                onCancel()
                return
            }
            val ext = url.pathExtension ?: ""
            val mime = mimeForExtension(ext)
            val name = url.lastPathComponent
            onResult(bytes, mime, name)
        } finally {
            if (accessing) {
                url.stopAccessingSecurityScopedResource()
            }
        }
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onCancel()
    }
}

private fun mimeForExtension(ext: String): String {
    val e = ext.lowercase()
    return when (e) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "gif" -> "image/gif"
        "webp" -> "image/webp"
        "heic" -> "image/heic"
        "mp4" -> "video/mp4"
        "mov" -> "video/quicktime"
        "m4v" -> "video/x-m4v"
        "mp3" -> "audio/mpeg"
        "m4a" -> "audio/m4a"
        "wav" -> "audio/wav"
        "aac" -> "audio/aac"
        else -> "application/octet-stream"
    }
}
