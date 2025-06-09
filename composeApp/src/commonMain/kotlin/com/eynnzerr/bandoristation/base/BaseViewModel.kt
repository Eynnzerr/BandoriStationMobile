package com.eynnzerr.bandoristation.base

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
interface UIState

@Stable
interface UIEvent

@Stable
interface UIEffect

abstract class BaseViewModel<State : UIState, Event: UIEvent, Effect: UIEffect>(
    val initialState: State,
) : ViewModel() {
    protected val internalState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> by lazy {
        internalState.onStart {
            viewModelScope.launch(Dispatchers.IO) {
                onStartStateFlow()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = initialState
        )
    }

    private val _effects = Channel<Effect>(capacity = Channel.CONFLATED)
    val effect = _effects.receiveAsFlow()

    // Used for one-time setup, when ViewModel is initialized.
    open suspend fun onInitialize() {}

    // Used for each-time setup, when reentering page. Will be called after loadInitialData().
    open suspend fun onStartStateFlow() {}

    init {
        viewModelScope.launch(Dispatchers.IO) {
            onInitialize()
        }
    }

    // process the input event and produce new state along with possible effect.
    abstract fun reduce(event: Event): Pair<State?, Effect?>

    fun sendEvent(event: Event) {
        val (newState, effect) = reduce(event)

        newState?.let {
            internalState.tryEmit(it)
        }
        effect?.let {
            sendEffect(it)
        }
    }

    fun sendEffect(effect: Effect) {
        _effects.trySend(effect)
    }

    fun sendState(state: State) {
        internalState.tryEmit(state)
    }
}