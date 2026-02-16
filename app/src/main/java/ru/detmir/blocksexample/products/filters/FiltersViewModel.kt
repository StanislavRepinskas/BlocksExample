package ru.detmir.blocksexample.products.filters

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.detmir.blocksexample.products.domain.model.FilterValue
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter

@HiltViewModel
class FiltersViewModel @Inject constructor() : ViewModel() {

    private var isInitialized = false
    private var draftFilter: ProductFilter = ProductFilter()

    private val _uiState = MutableStateFlow(
        UiState(
            availableFilters = emptyList(),
            selectedIdsByFilterId = emptyMap()
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun initialize(
        availableFilters: List<ProductAvailableFilter>,
        initialFilter: ProductFilter
    ) {
        if (isInitialized) return

        draftFilter = initialFilter.copy()
        _uiState.value = UiState(
            availableFilters = availableFilters,
            selectedIdsByFilterId = selectedIdsSnapshot(availableFilters)
        )
        isInitialized = true
    }

    fun onFilterCheckedChanged(
        filter: ProductAvailableFilter,
        value: FilterValue,
        checked: Boolean
    ) {
        if (!checked) {
            draftFilter.removeFilterValue(filter.filterId, value.id)
            publishState()
            return
        }

        when (filter.type) {
            ProductAvailableFilter.Type.Single -> {
                draftFilter.addFilterValue(
                    filterId = filter.filterId,
                    values = setOf(value)
                )
            }

            ProductAvailableFilter.Type.Multiple -> {
                val currentValues = filter.values
                    .filter { it.id in draftFilter.getFilterValue(filter.filterId) }
                    .toMutableSet()
                currentValues.add(value)
                draftFilter.addFilterValue(
                    filterId = filter.filterId,
                    values = currentValues
                )
            }
        }

        publishState()
    }

    fun getResultFilter(): ProductFilter = draftFilter.copy()

    private fun publishState() {
        _uiState.value = _uiState.value.copy(
            selectedIdsByFilterId = selectedIdsSnapshot(_uiState.value.availableFilters)
        )
    }

    private fun selectedIdsSnapshot(
        availableFilters: List<ProductAvailableFilter>
    ): Map<String, Set<String>> {
        return availableFilters.associate { filter ->
            filter.filterId to draftFilter.getFilterValue(filter.filterId)
        }
    }

    data class UiState(
        val availableFilters: List<ProductAvailableFilter>,
        val selectedIdsByFilterId: Map<String, Set<String>>
    )
}
