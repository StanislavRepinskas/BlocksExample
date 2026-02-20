package ru.detmir.blocksexample.framework.block.example

import kotlinx.coroutines.CoroutineScope
import ru.detmir.blocksexample.framework.block.block.BlockContext

class DmBlockContext(
    scope: CoroutineScope,
    val uuid: String,
) : BlockContext(scope)
