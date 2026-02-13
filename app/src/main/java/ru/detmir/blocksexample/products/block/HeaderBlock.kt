package ru.detmir.blocksexample.products.block

import ru.detmir.blocksexample.framework.block.Block

class HeaderBlock : Block<HeaderBlock.State, Unit>() {

    override fun getInitialState(): HeaderBlock.State {
        TODO("Not yet implemented")
    }

    data class State(
        val text: String
    )
}