package ru.detmir.blocksexample.products.block

import ru.detmir.blocksexample.framework.block.Block

class ProductsBlock : Block<ProductsBlock.State, Unit>() {

    override fun getInitialState(): ProductsBlock.State {
        TODO("Not yet implemented")
    }

    data class State(
        val text: String
    )
}