package ru.detmir.blocksexample.framework.block.block

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class Block<Context : BlockContext, State, Callbacks> {

    protected val _state = MutableStateFlow(getInitialState())
    val state = _state.asStateFlow()

    protected val callbacks: Callbacks
        get() = checkNotNull(_callbacks) { "Block callbacks are not attached" }
    private var _callbacks: Callbacks? = null

    protected lateinit var context: Context
        private set

    protected abstract fun getInitialState(): State

    protected fun updateState(block: (prev: State) -> (State)) {
        _state.value = block.invoke(_state.value)
    }

    /** Подлючение блока к ViewModel. */
    fun attach(context: Context, callbacks: Callbacks) {
        this.context = context
        this._callbacks = callbacks
    }

    /** Вызывается на ViewModel.start(). */
    open fun onStart() {}

    /** Вызывается когда показан UI. */
    open fun onUiStart() {}

    /** Вызывается когда UI скрыт. */
    open fun onUiStop() {}

    /** Вызывается когда уничтожается ViewModel. */
    open fun onCleared() {}
}
