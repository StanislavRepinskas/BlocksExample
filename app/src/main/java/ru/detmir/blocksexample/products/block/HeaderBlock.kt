package ru.detmir.blocksexample.products.block

import javax.inject.Inject
import ru.detmir.blocksexample.framework.block.Block

class HeaderBlock @Inject constructor() : Block<HeaderBlock.State, Unit>() {

    override fun getInitialState(): HeaderBlock.State {
        return State(text = "Products")
    }

    data class State(
        val text: String
    )
}
