package com.xvsx.shelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.xvsx.shelf.data.local.dataBase.roomContext
import com.xvsx.shelf.dependencyInjection.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            initKoin()
            roomContext = this
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}