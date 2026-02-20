package ru.detmir.blocksexample.products.block

import ru.detmir.blocksexample.framework.block.block.Block
import ru.detmir.blocksexample.framework.block.example.DmBlockContext
import javax.inject.Inject
import ru.detmir.blocksexample.framework.block.block.InputBlock
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter

class Example1 @Inject constructor() : Block<DmBlockContext, Example1.State, Unit>() {

    override fun getInitialState(): State {
        TODO("Not yet implemented")
    }

    data class State(val name: String)
}

class HeaderBlock @Inject constructor(
    //val feature: Feature
) : InputBlock<DmBlockContext, HeaderBlock.State, HeaderBlock.Input, HeaderBlock.Callbacks>() {

    private var availableFilters: List<ProductAvailableFilter> = emptyList()

    override fun getInitialState(): State {
        return State(
            text = "Products",
            selectedFilters = emptyList(),
            hasAvailableFilters = false
        )
    }

    fun onFiltersClick() {
        callbacks.onFiltersClick()
    }

    override fun setInput(input: Input) {
        availableFilters = input.availableFilters
        updateState { prev ->
            prev.copy(
                hasAvailableFilters = input.availableFilters.isNotEmpty(),
                selectedFilters = buildSelectedFilterChips(input.selectedFilters)
            )
        }
    }

    private fun buildSelectedFilterChips(selectedFilters: ProductFilter): List<SelectedFilterChip> {
        return availableFilters.mapNotNull { filter ->
            val selectedIds = selectedFilters.getFilterValue(filter.filterId)
            if (selectedIds.isEmpty()) {
                null
            } else {
                SelectedFilterChip(
                    filterId = filter.filterId,
                    title = filter.title
                )
            }
        }
    }

    data class State(
        val text: String,
        val selectedFilters: List<SelectedFilterChip>,
        val hasAvailableFilters: Boolean
    )

    data class SelectedFilterChip(
        val filterId: String,
        val title: String
    )

    data class Input(
        val availableFilters: List<ProductAvailableFilter>,
        val selectedFilters: ProductFilter
    )

    interface Callbacks {
        fun onFiltersClick()
    }
}
