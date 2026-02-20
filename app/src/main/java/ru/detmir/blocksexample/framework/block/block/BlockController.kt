package ru.detmir.blocksexample.framework.block.block

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BlockController<Context : BlockContext>(
    private val createBlockContext: () -> Context,
    private val onRegisterBlocks: (BlockRegistry<Context>) -> Unit,
    private val onUpdateBlocks: () -> Unit,
) {

    private var blocks: List<Block<Context, *, *>> = emptyList()
    private var collectBlockJob: Job? = null
    private var isBlocksRegistered = false

    fun start() {
        registerBlocks()
        blocks.forEach { it.onStart() }
    }

    fun onUiStart() {
        blocks.forEach { it.onUiStart() }
    }

    fun onUiStop() {
        blocks.forEach { it.onUiStop() }
    }

    fun onCleared() {
        collectBlockJob?.cancel()
        blocks.forEach { it.onCleared() }
    }

    private fun registerBlocks() {
        if (isBlocksRegistered) return

        collectBlockJob?.cancel()

        val registry = BlockRegistry<Context>()
        onRegisterBlocks(registry)
        val registrations = registry.getRegistrations()
        if (registrations.isEmpty()) {
            isBlocksRegistered = true
            return
        }

        val blockContext = createBlockContext()
        val scope = blockContext.scope
        blocks = registrations.map { it.block }
        registrations.forEach { it.attach(blockContext) }

        val observables = blocks
            .map { it.state }
            .toTypedArray()

        collectBlockJob = combine(*observables) { /* No-op */ }
            .onEach { onUpdateBlocks() }
            .launchIn(scope)
        isBlocksRegistered = true
    }
}
