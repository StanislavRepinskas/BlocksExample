package ru.detmir.blocksexample.framework.block.block

abstract class LoadBlock<State, Input, Callbacks> : Block<State, Callbacks>() {
    abstract suspend fun load(data: Input): Result<Unit>
}
