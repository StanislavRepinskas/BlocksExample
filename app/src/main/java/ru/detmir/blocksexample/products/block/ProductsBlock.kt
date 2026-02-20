package ru.detmir.blocksexample.products.block

import kotlinx.coroutines.launch
import javax.inject.Inject
import ru.detmir.blocksexample.framework.block.block.LoadBlock
import ru.detmir.blocksexample.framework.block.example.DmBlockContext
import ru.detmir.blocksexample.products.domain.model.Product
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter
import ru.detmir.blocksexample.products.domain.usecase.GetProductsUseCase

class ProductsBlock @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : LoadBlock<DmBlockContext, ProductsBlock.State, ProductFilter, ProductsBlock.Callbacks>() {

    override fun getInitialState(): State {
        return State(
            isLoading = true,
            products = emptyList(),
            error = null,
            selectedFilter = ProductFilter.initialWithCategoryShoes()
        )
    }

    override fun load(data: ProductFilter) {
        context.scope.launch {
            updateState { prev ->
                prev.copy(
                    isLoading = true,
                    error = null,
                    selectedFilter = data.copy()
                )
            }

            runCatching {
                getProductsUseCase.invoke(
                    filters = data,
                    page = 0
                )
            }.onSuccess { result ->
                updateState { prev ->
                    prev.copy(
                        isLoading = false,
                        products = result.products,
                        error = null
                    )
                }
                callbacks.onAvailableFiltersChanged(result.availableFilters)
            }.onFailure {
                updateState { prev ->
                    prev.copy(
                        isLoading = false,
                        error = "Что-то пошлло не так, попробуйте снова"
                    )
                }
            }
        }
    }

    data class State(
        val isLoading: Boolean,
        val products: List<Product>,
        val error: String?,
        val selectedFilter: ProductFilter
    )

    interface Callbacks {
        fun onAvailableFiltersChanged(filters: List<ProductAvailableFilter>)
    }
}
