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
            selectedFilterTitles = emptyList(),
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
                selectedFilterTitles = buildSelectedFilterTitles(selectedFilters)
            )
        }
    }

    fun onSelectedFiltersChanged(selectedFilters: ProductFilter) {
        updateState { prev ->
            prev.copy(
                selectedFilterTitles = buildSelectedFilterTitles(selectedFilters)
            )
        }
    }

    private fun buildSelectedFilterTitles(selectedFilters: ProductFilter): List<String> {
        return availableFilters.flatMap { filter ->
            val selectedIds = selectedFilters.getFilterValue(filter.filterId)
            filter.values
                .asSequence()
                .filter { it.id in selectedIds }
                .map { it.title }
                .toList()
        }
    }

    data class State(
        val text: String,
        val selectedFilterTitles: List<String>,
        val hasAvailableFilters: Boolean
    )

    interface Callbacks {
        fun onFiltersClick()
    }
}
