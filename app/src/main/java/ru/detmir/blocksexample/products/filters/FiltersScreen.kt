package ru.detmir.blocksexample.products.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.detmir.blocksexample.products.domain.model.FilterValue
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter

@Composable
fun FiltersScreen(
    availableFilters: List<ProductAvailableFilter>,
    initialFilter: ProductFilter,
    onApply: (ProductFilter) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    vm: FiltersViewModel = hiltViewModel()
) {
    LaunchedEffect(availableFilters, initialFilter) {
        vm.initialize(
            availableFilters = availableFilters,
            initialFilter = initialFilter
        )
    }

    val uiState by vm.uiState.collectAsState()

    FiltersContent(
        uiState = uiState,
        onCheckedChange = vm::onFilterCheckedChanged,
        onApply = { onApply(vm.getResultFilter()) },
        onCancel = onCancel,
        modifier = modifier
    )
}

@Composable
fun FiltersContent(
    uiState: FiltersViewModel.UiState,
    onCheckedChange: (ProductAvailableFilter, FilterValue, Boolean) -> Unit,
    onApply: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Фильтры",
                style = MaterialTheme.typography.headlineSmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onCancel) {
                    Text(text = "Отмена")
                }
                Button(onClick = onApply) {
                    Text(text = "Применить")
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.availableFilters, key = { filter -> filter.filterId }) { filter ->
                Text(
                    text = filter.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                filter.values.forEach { value ->
                    val selectedIds = uiState.selectedIdsByFilterId[filter.filterId].orEmpty()
                    val isChecked = value.id in selectedIds

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                onCheckedChange(filter, value, checked)
                            }
                        )
                        Text(text = value.title)
                    }
                }
            }
        }
    }
}
