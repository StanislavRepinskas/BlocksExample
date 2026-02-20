package ru.detmir.blocksexample.framework.block.viewmodel

import ru.detmir.blocksexample.framework.block.block.BlockContext
import ru.detmir.blocksexample.framework.block.block.BlockController
import ru.detmir.blocksexample.framework.block.block.BlockRegistry

interface BlockViewModel<Context : BlockContext> {

    val blockController: BlockController<Context>

    fun createBlockContext(): Context

    fun onRegisterBlocks(registry: BlockRegistry<Context>)

    fun onUpdateBlocks()

    fun createBlockController(): BlockController<Context> {
        return BlockController(
            createBlockContext = ::createBlockContext,
            onRegisterBlocks = ::onRegisterBlocks,
            onUpdateBlocks = ::onUpdateBlocks,
        )
    }

    fun start() {
        blockController.start()
    }

    fun onUiStart() {
        blockController.onUiStart()
    }

    fun onUiStop() {
        blockController.onUiStop()
    }

    fun onBlockViewModelCleared() {
        blockController.onCleared()
    }
}
