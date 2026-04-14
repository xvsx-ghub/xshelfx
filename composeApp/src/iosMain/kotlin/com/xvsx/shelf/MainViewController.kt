package com.xvsx.shelf

import androidx.compose.ui.window.ComposeUIViewController
import com.xvsx.shelf.dependencyInjection.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}