package ru.detmir.blocksexample.framework.block.block

abstract class InputBlock<Context : BlockContext, State, Input, Callbacks> :
    Block<Context, State, Callbacks>() {
    abstract fun setInput(input: Input)
}
