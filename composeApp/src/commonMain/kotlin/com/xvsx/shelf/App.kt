package com.xvsx.shelf

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.xvsx.shelf.push.AppBadgeService
import com.xvsx.shelf.push.FcmKotlinTokenFetchSkipped
import com.xvsx.shelf.push.FcmPushCoordinator
import com.xvsx.shelf.push.fetchCurrentPushTokenForRegistration
import com.xvsx.shelf.userInterface.screen.SplashScreen
import com.xvsx.shelf.util.Logger
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val appBadgeService: AppBadgeService = koinInject()
    LaunchedEffect(appBadgeService) {
        appBadgeService.syncStoredCountToPlatform()
    }
    val fcmPushCoordinator: FcmPushCoordinator = koinInject()
    LaunchedEffect(fcmPushCoordinator) {
        fetchCurrentPushTokenForRegistration { result ->
            result.onSuccess { token -> fcmPushCoordinator.notifyTokenReceived(token) }
            result.onFailure { error ->
                if (error is FcmKotlinTokenFetchSkipped) return@onFailure
                Logger.e("App", "fetchCurrentPushTokenForRegistration failed: ${error.message}", error)
            }
        }
    }
    MaterialTheme {
        val splashScreen: SplashScreen = koinInject()
        Navigator(splashScreen)
    }
}