package com.xvsx.shelf.chat

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openMediaExternally(absolutePath: String, mimeType: String) {
    val url = NSURL.fileURLWithPath(absolutePath)
    UIApplication.sharedApplication.openURL(url, mapOf<Any?, Any?>(), null)
}
