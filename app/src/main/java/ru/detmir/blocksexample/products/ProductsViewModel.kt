package ru.detmir.blocksexample.products

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.detmir.blocksexample.framework.UIStatus
import ru.detmir.blocksexample.framework.block.viewmodel.BlockRegistry
import ru.detmir.blocksexample.framework.block.viewmodel.BlockViewModel
import ru.detmir.blocksexample.products.block.Example1
import ru.detmir.blocksexample.products.block.HeaderBlock
import ru.detmir.blocksexample.products.block.ProductsBlock
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter

/*
* 1) Внутрение блоки?
* 2) Фича флаги
* 3) ?
* */

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val headerBlock: HeaderBlock,
    private val productsBlock: ProductsBlock,
    private val example1: Example1
) : BlockViewModel() {

    private var isStarted = false
    private var availableFilters: List<ProductAvailableFilter> = emptyList()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    private val _uiState = MutableStateFlow(
        UiState(
            uiStatus = UIStatus.LOADING,
            header = headerBlock.state.value,
            products = productsBlock.state.value,
            availableFilters = availableFilters
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    override fun onRegisterBlocks(registry: BlockRegistry) {
        registry.register(headerBlock, object : HeaderBlock.Callbacks {
            override fun onFiltersClick() {
                viewModelScope.launch {
                    _navigationEvents.emit(NavigationEvent.OpenFilters)
                }
            }
        })

        registry.register(productsBlock, object : ProductsBlock.Callbacks {
            override fun onAvailableFiltersChanged(filters: List<ProductAvailableFilter>) {
                availableFilters = filters
                headerBlock.setInput(
                    HeaderBlock.Input(
                        availableFilters = filters,
                        selectedFilters = productsBlock.state.value.selectedFilter
                    )
                )
            }
        })

        registry.register(example1)
    }

    override fun start() {
        super.start()
        if (isStarted) return
        isStarted = true

        productsBlock.load(productsBlock.state.value.selectedFilter)
    }

    override fun onUpdateBlocks() {
        val productsState = productsBlock.state.value

        val uiStatus = when {
            productsState.isLoading && productsState.products.isEmpty() -> UIStatus.LOADING
            productsState.error != null && productsState.products.isEmpty() -> UIStatus.ERROR
            else -> UIStatus.SUCCESS
        }
        _uiState.value = UiState(
            uiStatus = uiStatus,
            header = headerBlock.state.value,
            products = productsState,
            availableFilters = availableFilters
        )
    }

    fun retryProductsLoading() {
        viewModelScope.launch {
            productsBlock.load(productsBlock.state.value.selectedFilter)
        }
    }

    fun refreshProducts() {
        viewModelScope.launch {
            productsBlock.load(productsBlock.state.value.selectedFilter)
        }
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
        headerBlock.setInput(
            HeaderBlock.Input(
                availableFilters = availableFilters,
                selectedFilters = filters
            )
        )
        viewModelScope.launch {
            productsBlock.load(filters)
        }
    }

    data class UiState(
        val uiStatus: UIStatus,
        val header: HeaderBlock.State,
        val products: ProductsBlock.State,
        val availableFilters: List<ProductAvailableFilter>
    )

    sealed interface NavigationEvent {
        data object OpenFilters : NavigationEvent
    }
}
