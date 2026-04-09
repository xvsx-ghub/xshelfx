package com.xvsx.shelf

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.xvsx.shelf.chat.PublicChatScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        PublicChatScreen()
    }
}