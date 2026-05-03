package com.xvsx.shelf.dependencyInjection

import com.russhwolf.settings.Settings
import com.xvsx.shelf.data.local.RepositoryLocal
import com.xvsx.shelf.data.local.SettingsManager
import com.xvsx.shelf.data.local.dataBase.getRoomDatabase
import com.xvsx.shelf.data.remote.RepositoryRemote
import com.xvsx.shelf.data.remote.http.Http
import com.xvsx.shelf.data.remote.stomp.StompManager
import com.xvsx.shelf.data.useCase.LoginUseCase
import com.xvsx.shelf.push.AppBadgeService
import com.xvsx.shelf.push.FcmPushCoordinator
import com.xvsx.shelf.push.PushTokenRegistrar
import com.xvsx.shelf.userInterface.screen.ChatScreen
import com.xvsx.shelf.userInterface.screen.ContactListScreen
import com.xvsx.shelf.userInterface.screen.SplashScreen
import com.xvsx.shelf.userInterface.viewModel.ChatViewModel
import com.xvsx.shelf.userInterface.viewModel.ContactListViewModel
import com.xvsx.shelf.userInterface.viewModel.SplashViewModel
import com.xvsx.shelf.util.System
import org.koin.dsl.module

val baseApplicationModule = module {
    single { SplashScreen() }
    single { ChatScreen() }
    single { ContactListScreen() }

    single { SplashViewModel(get (), get()) }
    single { ChatViewModel(get (), get(), get(), get()) }
    single { ContactListViewModel(get(), get(),get()) }

    single<Settings> { Settings() }
    single { SettingsManager(get()) }
    single { getRoomDatabase() }
    single { Http(get()) }
    single { StompManager(get(), get()) }

    single { System() }

    single { PushTokenRegistrar(get(), get(), get()) }
    single { AppBadgeService(get()) }
    single { FcmPushCoordinator(get(), get()) }
    single { LoginUseCase(get(), get(), get()) }

    single { RepositoryLocal(get(), get(), get()) }
    single { RepositoryRemote(get()) }
}