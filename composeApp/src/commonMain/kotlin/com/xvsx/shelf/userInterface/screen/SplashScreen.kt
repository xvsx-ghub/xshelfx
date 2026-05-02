package com.xvsx.shelf.userInterface.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.xvsx.shelf.push.PushTokenRegistrar
import com.xvsx.shelf.userInterface.viewModel.SplashViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import shelf.composeapp.generated.resources.Res
import shelf.composeapp.generated.resources.ic_logo

class SplashScreen() : Screen {
    companion object {
        const val TAG = "SplashScreen"
    }

    @OptIn(ExperimentalComposeUiApi::class, InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val splashViewModel: SplashViewModel = koinInject()
        val chatScreen: ChatScreen = koinInject()
        val pushTokenRegistrar: PushTokenRegistrar = koinInject()
        val navigator = LocalNavigator.current

        LaunchedEffect(Unit) {
            splashViewModel.setBaseUrl("https://compellingly-presynsacral-albertine.ngrok-free.dev/")
            pushTokenRegistrar.flushPendingAfterLogin()
            navigator?.push(chatScreen)
        }

        BackHandler(enabled = true) {}

        Box(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing).background(Color.Black),
        ) {
            ContentView()
        }
    }

    @Composable
    private fun ContentView(
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.ic_logo),
                contentDescription = "logo"
            )
        }
    }
}