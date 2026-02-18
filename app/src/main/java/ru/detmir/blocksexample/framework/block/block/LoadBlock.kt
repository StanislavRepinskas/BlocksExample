package ru.detmir.blocksexample.framework.block.block

abstract class LoadBlock<State, Input, Callbacks> : Block<State, Callbacks>() {
    abstract fun load(data: Input)
}
