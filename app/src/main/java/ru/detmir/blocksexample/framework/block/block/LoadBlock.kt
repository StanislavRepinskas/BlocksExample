package ru.detmir.blocksexample.framework.block.block

abstract class LoadBlock<Context : BlockContext, State, Input, Callbacks> :
    Block<Context, State, Callbacks>() {
    abstract fun load(data: Input)
}
