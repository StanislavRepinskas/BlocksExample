package ru.detmir.blocksexample.framework.block

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

abstract class BlockViewModel : ViewModel() {

    protected val uuid = UUID.randomUUID().toString()
    private var blocks: List<Block<*, *>> = emptyList()

    protected fun registerBlocks(blocks: List<Block<*, *>>) {
        this.blocks = blocks
        this.blocks.forEach { it.onCreate() }

        val blockContext = BlockContext(
            uuid = uuid,
            scope = viewModelScope
        )

        this.blocks.forEach {
            it.context = blockContext
            it.state
                .onEach { onBlocksUpdate() }
                .launchIn(viewModelScope)
        }
        onBlocksUpdate()
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
        blocks.forEach { it.onDestroy() }
    }
}
