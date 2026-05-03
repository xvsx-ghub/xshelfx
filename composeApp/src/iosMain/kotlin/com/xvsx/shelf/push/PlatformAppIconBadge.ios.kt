package com.xvsx.shelf.push

import platform.UIKit.UIApplication
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun applyAppIconBadgeCount(count: Int) {
    val capped = count.coerceIn(0, 999)
    dispatch_async(dispatch_get_main_queue()) {
        UIApplication.sharedApplication.applicationIconBadgeNumber = capped.toLong()
    }
}