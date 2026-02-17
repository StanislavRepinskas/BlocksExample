package ru.detmir.blocksexample.framework.block

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BlockContext(
    val uuid: String,
    val scope: CoroutineScope
)

abstract class Block<State, Callbacks> {

    protected val _state = MutableStateFlow(getInitialState())
    val state = _state.asStateFlow()

    var callbacks: Callbacks? = null

    lateinit var context: BlockContext

    protected abstract fun getInitialState(): State

    protected fun updateState(block: (prev: State) -> (State)) {
        _state.value = block.invoke(_state.value)
    }

    // Lifecycle
    open fun onCreate() {}
    open fun onStart() {}
    open fun onStop() {}
    open fun onDestroy() {}
}