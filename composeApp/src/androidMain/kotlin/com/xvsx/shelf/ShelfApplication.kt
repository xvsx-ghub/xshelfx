package com.xvsx.shelf

import android.app.Application
import com.xvsx.shelf.dependencyInjection.initKoin

class ShelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
