package com.xvsx.shelf

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.xvsx.shelf.userInterface.screen.SplashScreen
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    MaterialTheme {
        val splashScreen: SplashScreen = koinInject()
        Navigator(splashScreen)
    }
}