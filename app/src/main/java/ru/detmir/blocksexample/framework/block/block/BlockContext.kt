package ru.detmir.blocksexample.framework.block.block

import kotlinx.coroutines.CoroutineScope

data class BlockContext(
    val uuid: String,
    val scope: CoroutineScope
)