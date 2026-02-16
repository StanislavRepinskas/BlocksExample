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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.detmir.blocksexample.products.domain.model.FilterValue
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter

@Composable
fun FiltersScreen(
    availableFilters: List<ProductAvailableFilter>,
    initialFilter: ProductFilter,
    onApply: (ProductFilter) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val draftFilter = remember(initialFilter) { initialFilter.copy() }
    var revision by remember { mutableIntStateOf(0) }
    val selectedSnapshot = remember(revision) { draftFilter.getSelectedValues() }

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
                Button(onClick = { onApply(draftFilter.copy()) }) {
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
            items(availableFilters, key = { filter -> filter.filterId }) { filter ->
                Text(
                    text = filter.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                filter.values.forEach { value ->
                    val selectedIds = selectedSnapshot[filter.filterId]?.map { it.id }?.toSet().orEmpty()
                    val isChecked = value.id in selectedIds
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                updateDraftFilter(
                                    draftFilter = draftFilter,
                                    filter = filter,
                                    value = value,
                                    checked = checked
                                )
                                revision++
                            }
                        )
                        Text(text = value.title)
                    }
                }
            }
        }
    }
}

private fun updateDraftFilter(
    draftFilter: ProductFilter,
    filter: ProductAvailableFilter,
    value: FilterValue,
    checked: Boolean
) {
    if (!checked) {
        draftFilter.removeFilterValue(filter.filterId, value.id)
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
            val currentValues = filter.values.filter {
                it.id in draftFilter.getFilterValue(filter.filterId)
            }.toMutableSet()
            currentValues.add(value)
            draftFilter.addFilterValue(
                filterId = filter.filterId,
                values = currentValues
            )
        }
    }
}
