package ru.detmir.blocksexample.products

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.detmir.blocksexample.framework.block.viewmodel.BlockRegistry
import ru.detmir.blocksexample.framework.block.viewmodel.BlockViewModel
import ru.detmir.blocksexample.products.block.HeaderBlock
import ru.detmir.blocksexample.products.block.ProductsBlock
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val headerBlock: HeaderBlock,
    private val productsBlock: ProductsBlock
) : BlockViewModel() {

    private var isStarted = false
    private var availableFilters: List<ProductAvailableFilter> = emptyList()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    private val _uiState = MutableStateFlow(
        UiState(
            header = headerBlock.state.value,
            products = productsBlock.state.value,
            availableFilters = availableFilters
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    override fun onRegisterBlocks(registry: BlockRegistry) {
        registry.add(headerBlock, object : HeaderBlock.Callbacks {
            override fun onFiltersClick() {
                viewModelScope.launch {
                    _navigationEvents.emit(NavigationEvent.OpenFilters)
                }
            }
        })

        registry.add(productsBlock, object : ProductsBlock.Callbacks {
            override fun onAvailableFiltersChanged(filters: List<ProductAvailableFilter>) {
                availableFilters = filters
                headerBlock.onAvailableFiltersChanged(
                    filters = filters,
                    selectedFilters = productsBlock.state.value.selectedFilter
                )
            }

            override fun onLoadSuccess() {
            }

            override fun onLoadError() {
            }
        })
    }

    override fun start() {
        super.start()
        if (isStarted) return
        isStarted = true
        productsBlock.load(productsBlock.state.value.selectedFilter)
    }

    override fun onUpdateBlocks() {
        _uiState.value = UiState(
            header = headerBlock.state.value,
            products = productsBlock.state.value,
            availableFilters = availableFilters
        )
    }

    fun retryProductsLoading() {
        productsBlock.reload()
    }

    fun refreshProducts() {
        productsBlock.reload()
    }

    fun onFiltersClick() {
        headerBlock.onFiltersClick()
    }

    fun onSelectedFilterRemove(filterId: String) {
        val updatedFilter = productsBlock.state.value.selectedFilter.copy().apply {
            removeFilterValue(filterId)
        }
        applyFilters(updatedFilter)
    }

    fun applyFilters(filters: ProductFilter) {
        headerBlock.onSelectedFiltersChanged(filters)
        productsBlock.load(filters)
    }

    data class UiState(
        val header: HeaderBlock.State,
        val products: ProductsBlock.State,
        val availableFilters: List<ProductAvailableFilter>
    )

    sealed interface NavigationEvent {
        data object OpenFilters : NavigationEvent
    }
}
