package ru.detmir.blocksexample.framework.block

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

abstract class BlockViewModel : ViewModel() {

    protected var uuid = UUID.randomUUID().toString()
    private var blocks: List<Block<*, *>> = emptyList()

    init {
        val blocks = onRegisterBlocks()
        blocks.forEach { it.onCreate() }

        val blockContext = BlockContext(
            uuid = uuid,
            coroutineScope = viewModelScope
        )

        val states = mutableListOf<StateFlow<Any?>>()
        blocks.forEach {
            it.context = blockContext
            states += it.state
        }

        combine(*states.toTypedArray()) {/* No-op */ }
            .onEach { onBlocksUpdate() }
            .launchIn(viewModelScope)
    }

    protected abstract fun onRegisterBlocks(): List<Block<*, *>>

    protected abstract fun onBlocksUpdate()

    fun start() {

    }

    /** Вызывается при ЖЗ OnStart */
    fun onStart() {
        blocks.forEach { it.onStart() }
    }

    /** Вызывается при ЖЗ OnStop */
    fun onStop() {
        blocks.forEach { it.onStop() }
    }

    override fun onCleared() {
        super.onCleared()
        blocks.forEach { it.onDestroy() }
    }
}