package ru.detmir.blocksexample.products.block

import javax.inject.Inject
import ru.detmir.blocksexample.framework.block.Block

class ProductsBlock @Inject constructor() : Block<ProductsBlock.State, Unit>() {

    override fun getInitialState(): State {
        return State(text = "No products yet")
    }

    data class State(
        val text: String
    )
}
