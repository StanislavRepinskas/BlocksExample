package ru.detmir.blocksexample.products.filters

import androidx.lifecycle.SavedStateHandle
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
class FiltersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val availableFilters: List<ProductAvailableFilter> =
        savedStateHandle.get<ArrayList<ProductAvailableFilter>>(FiltersNavContract.ARG_AVAILABLE_FILTERS)
            ?.toList()
            .orEmpty()

    private var draftFilter: ProductFilter =
        savedStateHandle.get<ProductFilter>(FiltersNavContract.ARG_INITIAL_FILTER)
            ?.copy()
            ?: ProductFilter()

    private val _uiState = MutableStateFlow(
        UiState(
            availableFilters = availableFilters,
            selectedIdsByFilterId = selectedIdsSnapshot()
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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
            selectedIdsByFilterId = selectedIdsSnapshot()
        )
    }

    private fun selectedIdsSnapshot(): Map<String, Set<String>> {
        return availableFilters.associate { filter ->
            filter.filterId to draftFilter.getFilterValue(filter.filterId)
        }
    }

    data class UiState(
        val availableFilters: List<ProductAvailableFilter>,
        val selectedIdsByFilterId: Map<String, Set<String>>
    )
}
