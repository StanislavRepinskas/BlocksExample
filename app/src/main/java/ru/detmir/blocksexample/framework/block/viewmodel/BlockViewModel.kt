package ru.detmir.blocksexample.framework.block.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.detmir.blocksexample.framework.block.block.Block
import ru.detmir.blocksexample.framework.block.block.BlockContext
import java.util.UUID

abstract class BlockViewModel : ViewModel() {

    protected val uuid = UUID.randomUUID().toString()
    private var blocks: List<Block<*, *>> = emptyList()
    private var collectBlockJob: Job? = null
    private var isBlocksRegistered = false

    private fun registerBlocks() {
        if (isBlocksRegistered) return

        collectBlockJob?.cancel()

        val registry = BlockRegistry()
        onRegisterBlocks(registry)
        val registrations = registry.getRegistrations()
        if (registrations.isEmpty()) {
            isBlocksRegistered = true
            return
        }

        val blockContext = BlockContext(
            uuid = uuid,
            scope = viewModelScope
        )

        blocks = registrations.map { it.block }
        registrations.forEach { it.attach(blockContext) }

        val observables = this.blocks
            .map { it.state }
            .toTypedArray()

        collectBlockJob = combine(*observables) { /* No-op */ }
            .onEach { onUpdateBlocks() }
            .launchIn(viewModelScope)
        isBlocksRegistered = true
    }

    protected abstract fun onRegisterBlocks(registry: BlockRegistry)
    protected abstract fun onUpdateBlocks()

    open fun start() {
        registerBlocks()
        blocks.forEach { it.onStart() }
    }

    fun onStart() {
        blocks.forEach { it.onUiStart() }
    }

    fun onStop() {
        blocks.forEach { it.onUiStop() }
    }

    override fun onCleared() {
        super.onCleared()
        collectBlockJob?.cancel()
        blocks.forEach { it.onCleared() }
    }
}
