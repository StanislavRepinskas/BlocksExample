package ru.detmir.blocksexample.framework.block.viewmodel

import ru.detmir.blocksexample.framework.block.block.BlockContext
import ru.detmir.blocksexample.framework.block.block.BlockController

fun <Context : BlockContext> BlockViewModel<Context>.lazyBlockController(
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
): Lazy<BlockController<Context>> {
    return lazy(mode) { createBlockController() }
}
