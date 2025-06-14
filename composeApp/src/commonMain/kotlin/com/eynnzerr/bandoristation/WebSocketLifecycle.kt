package com.eynnzerr.bandoristation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.eynnzerr.bandoristation.data.remote.websocket.WebSocketClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun WebSocketLifecycleHandler(
    coroutineScope: CoroutineScope = rememberCoroutineScope { Dispatchers.IO + SupervisorJob() }
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val webSocketClient: WebSocketClient = koinInject()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                        throwable.printStackTrace()
                    }

                    coroutineScope.launch(exceptionHandler) {
                        webSocketClient.connect()
                    }
                }
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
