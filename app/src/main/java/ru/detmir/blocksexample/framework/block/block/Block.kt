package ru.detmir.blocksexample.framework.block.block

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class Block<State, Callbacks> {

    protected val _state = MutableStateFlow(getInitialState())
    val state = _state.asStateFlow()

    protected val callbacks: Callbacks
        get() = checkNotNull(_callbacks) { "Block callbacks are not attached" }
    private var _callbacks: Callbacks? = null

    protected lateinit var context: BlockContext
        private set

    protected abstract fun getInitialState(): State

    protected fun updateState(block: (prev: State) -> (State)) {
        _state.value = block.invoke(_state.value)
    }

    fun attach(context: BlockContext, callbacks: Callbacks) {
        this.context = context
        this._callbacks = callbacks
    }

    /** Вызывается когда блок готов к работе, после после регистрации блока на ViewModel. */
    open fun onCreate() {}
    /** Вызывается когда блок готов к работе, после после регистрации блока на ViewModel. */
    open fun onStart() {}
    open fun onStop() {}
    open fun onDestroy() {}
}
