package ru.detmir.blocksexample.products.block

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import ru.detmir.blocksexample.framework.block.Block
import ru.detmir.blocksexample.framework.block.LoadableBlock
import ru.detmir.blocksexample.products.domain.usecase.GetProductsUseCase

class ProductsBlock @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : LoadableBlock<ProductsBlock.State, String, LoadableBlock.Callbacks>() {

    private var data = ""

    override fun getInitialState(): State {
        return State(text = "No products yet")
    }

    override fun load(data: String) {
        this.data = data
        context?.coroutineScope?.launch {
            updateState { prev ->
                prev.copy(text = "Loading products")
            }
            getProductsUseCase.invoke()
        }
    }

    override fun reload() {
        load(data)
    }

    data class State(
        val text: String
    )
}
