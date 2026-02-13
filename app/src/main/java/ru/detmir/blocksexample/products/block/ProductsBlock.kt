package ru.detmir.blocksexample.products.block

import kotlinx.coroutines.launch
import javax.inject.Inject
import ru.detmir.blocksexample.framework.block.LoadableBlock
import ru.detmir.blocksexample.products.domain.model.Product
import ru.detmir.blocksexample.products.domain.usecase.GetProductsUseCase

class ProductsBlock @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : LoadableBlock<ProductsBlock.State, Unit, LoadableBlock.Callbacks>() {

    override fun getInitialState(): State {
        return State(
            isLoading = false,
            products = emptyList(),
            error = null
        )
    }

    override fun load(data: Unit) {
        context?.coroutineScope?.launch {
            updateState { prev ->
                prev.copy(
                    isLoading = true,
                    error = null
                )
            }

            runCatching { getProductsUseCase.invoke() }
                .onSuccess { products ->
                    updateState { prev ->
                        prev.copy(
                            isLoading = false,
                            products = products,
                            error = null
                        )
                    }
                    callbacks?.onLoadSuccess()
                }
                .onFailure {
                    updateState { prev ->
                        prev.copy(
                            isLoading = false,
                            error = "Ошибка загрузки"
                        )
                    }
                    callbacks?.onLoadError()
                }
        }
    }

    override fun reload() {
        load(Unit)
    }

    data class State(
        val isLoading: Boolean,
        val products: List<Product>,
        val error: String?
    )
}
