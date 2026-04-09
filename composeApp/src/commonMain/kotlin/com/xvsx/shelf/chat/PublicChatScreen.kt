package com.xvsx.shelf.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicChatScreen() {
    PlatformChatHook()
    val repository = rememberChatRepository()
    val storage = rememberPlatformFileStorage()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var draft by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    val picker = rememberMediaPickController { bytes, mime, name ->
        scope.launch {
            busy = true
            runCatching { repository.sendAttachment(bytes, mime, name) }
                .onSuccess { messages = messages + it }
            busy = false
        }
    }

    LaunchedEffect(Unit) {
        messages = repository.loadMessages()
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Public chat") },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding(),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageRow(
                        message = message,
                        absoluteAttachmentPath = message.attachment?.let { storage.absolutePath(it.relativePath) },
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                IconButton(onClick = { picker.pickImage() }, enabled = !busy) {
                    Text("Photo", style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = { picker.pickVideo() }, enabled = !busy) {
                    Text("Video", style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = { picker.pickAudio() }, enabled = !busy) {
                    Text("Audio", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message") },
                    singleLine = false,
                    maxLines = 4,
                )
                TextButton(
                    onClick = {
                        val text = draft
                        if (text.isBlank() || busy) return@TextButton
                        scope.launch {
                            busy = true
                            runCatching { repository.sendText(text) }
                                .onSuccess {
                                    messages = messages + it
                                    draft = ""
                                }
                            busy = false
                        }
                    },
                    enabled = !busy && draft.isNotBlank(),
                ) {
                    Text("Send")
                }
            }

            if (busy) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                )
            }
        }
    }
}

@Composable
private fun ChatMessageRow(
    message: ChatMessage,
    absoluteAttachmentPath: String?,
) {
    val attachment = message.attachment
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = formatTime(message.timestampMillis),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (message.text.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = message.text, style = MaterialTheme.typography.bodyLarge)
            }
            if (attachment != null && absoluteAttachmentPath != null) {
                Spacer(modifier = Modifier.height(8.dp))
                when (attachment.kind) {
                    AttachmentKind.IMAGE -> ChatImage(absoluteAttachmentPath)
                    AttachmentKind.VIDEO -> ChatVideo(attachment, absoluteAttachmentPath)
                    AttachmentKind.AUDIO -> ChatAudio(attachment, absoluteAttachmentPath)
                }
            }
        }
    }
}

@Composable
private fun ChatImage(absolutePath: String) {
    val context = LocalPlatformContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(absolutePath)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 320.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun ChatVideo(attachment: StoredAttachment, absolutePath: String) {
    Column {
        Text(
            text = attachment.originalName ?: "Video",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = { openMediaExternally(absolutePath, attachment.mimeType) }) {
            Text("Play in external viewer")
        }
    }
}

@Composable
private fun ChatAudio(attachment: StoredAttachment, absolutePath: String) {
    Column {
        Text(
            text = attachment.originalName ?: "Audio",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        InlineAudioPlayer(absolutePath, attachment.mimeType)
    }
}

@Composable
expect fun InlineAudioPlayer(absolutePath: String, mimeType: String)

internal expect fun formatTime(timestampMillis: Long): String
