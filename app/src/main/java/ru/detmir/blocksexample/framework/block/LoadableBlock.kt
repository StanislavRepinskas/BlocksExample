package ru.detmir.blocksexample.framework.block

abstract class LoadableBlock<State, Input, Callbacks : LoadableBlock.Callbacks> : Block<State, Callbacks>() {

    abstract fun load(data: Input)
    abstract fun reload()

    interface Callbacks {
        fun onLoadSuccess()
        fun onLoadError()
    }
}