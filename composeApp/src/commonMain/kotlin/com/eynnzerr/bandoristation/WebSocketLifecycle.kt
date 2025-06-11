package com.eynnzerr.bandoristation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun WebSocketLifecycleHandler() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val webSocketClient: WebSocketClient = koinInject()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START ->
                    coroutineScope.launch { webSocketClient.connect() }
                Lifecycle.Event.ON_STOP ->
                    webSocketClient.disconnect()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
