package ru.detmir.blocksexample.products.block

import javax.inject.Inject
import ru.detmir.blocksexample.framework.block.Block
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter

class HeaderBlock @Inject constructor() : Block<HeaderBlock.State, HeaderBlock.Callbacks>() {

    private var availableFilters: List<ProductAvailableFilter> = emptyList()

    override fun getInitialState(): State {
        return State(
            text = "Products",
            selectedFilters = emptyList(),
            hasAvailableFilters = false
        )
    }

    fun onFiltersClick() {
        callbacks?.onFiltersClick()
    }

    fun onAvailableFiltersChanged(filters: List<ProductAvailableFilter>, selectedFilters: ProductFilter) {
        availableFilters = filters
        updateState { prev ->
            prev.copy(
                hasAvailableFilters = filters.isNotEmpty(),
                selectedFilters = buildSelectedFilterChips(selectedFilters)
            )
        }
    }

    fun onSelectedFiltersChanged(selectedFilters: ProductFilter) {
        updateState { prev ->
            prev.copy(
                selectedFilters = buildSelectedFilterChips(selectedFilters)
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

    interface Callbacks {
        fun onFiltersClick()
    }
}
