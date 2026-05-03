package com.xvsx.shelf

import android.app.Application
import com.xvsx.shelf.dependencyInjection.initKoin
import com.xvsx.shelf.push.setAppBadgeAndroidContext

class ShelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setAppBadgeAndroidContext(this)
        initKoin()
    }
}