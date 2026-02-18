package ru.detmir.blocksexample.framework.block.block

abstract class InputBlock<State, Input, Callbacks> : Block<State, Callbacks>() {
    abstract fun setInput(input: Input)
}
