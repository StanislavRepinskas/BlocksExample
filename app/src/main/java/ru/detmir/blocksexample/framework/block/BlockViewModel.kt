package ru.detmir.blocksexample.framework.block

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

abstract class BlockViewModel : ViewModel() {

    protected val uuid = UUID.randomUUID().toString()
    private var blocks: List<Block<*, *>> = emptyList()
    private var collectBlockJob: Job? = null

    protected fun registerBlocks(vararg blocks: Block<*, *>) {
        collectBlockJob?.cancel()
        if (blocks.isEmpty()) return

        val blockContext = BlockContext(
            uuid = uuid,
            scope = viewModelScope
        )

        this.blocks = blocks.toList()
        blocks.forEach {
            it.context = blockContext
        }

        blocks.forEach { it.onCreate() }

        val observables = blocks
            .map { it.state }
            .toTypedArray()

        collectBlockJob = combine(*observables) { /* No-op */ }
            .onEach { onBlocksUpdate() }
            .launchIn(viewModelScope)
    }

    protected abstract fun onBlocksUpdate()

    open fun start() {
    }

    fun onStart() {
        blocks.forEach { it.onStart() }
    }

    fun onStop() {
        blocks.forEach { it.onStop() }
    }

    override fun onCleared() {
        super.onCleared()
        collectBlockJob?.cancel()
        blocks.forEach { it.onDestroy() }
    }
}
