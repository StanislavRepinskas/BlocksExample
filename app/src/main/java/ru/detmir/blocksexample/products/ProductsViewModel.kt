package ru.detmir.blocksexample.products

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.detmir.blocksexample.framework.block.BlockViewModel
import ru.detmir.blocksexample.products.block.HeaderBlock
import ru.detmir.blocksexample.products.block.ProductsBlock

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val headerBlock: HeaderBlock,
    private val productsBlock: ProductsBlock
) : BlockViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            header = headerBlock.state.value,
            products = productsBlock.state.value
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        registerBlocks(listOf(headerBlock, productsBlock))
    }

    override fun onBlocksUpdate() {
        _uiState.value = UiState(
            header = headerBlock.state.value,
            products = productsBlock.state.value
        )
    }

    data class UiState(
        val header: HeaderBlock.State,
        val products: ProductsBlock.State
    )
}
