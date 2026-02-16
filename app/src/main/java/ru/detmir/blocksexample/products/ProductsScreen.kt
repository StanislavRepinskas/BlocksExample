package ru.detmir.blocksexample.products

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import ru.detmir.blocksexample.products.ProductsViewModel.UiState
import ru.detmir.blocksexample.products.domain.model.Product
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter
import java.text.DecimalFormatSymbols
import ru.detmir.blocksexample.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onOpenFilters: (List<ProductAvailableFilter>, ProductFilter) -> Unit,
    pendingAppliedFilter: ProductFilter?,
    onAppliedFilterConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    vm: ProductsViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(vm) {
        vm.start()
    }

    LaunchedEffect(vm) {
        vm.navigationEvents.collect { event ->
            when (event) {
                ProductsViewModel.NavigationEvent.OpenFilters -> {
                    onOpenFilters(uiState.availableFilters, uiState.products.selectedFilter)
                }
            }
        }
    }

    LaunchedEffect(pendingAppliedFilter) {
        val appliedFilter = pendingAppliedFilter ?: return@LaunchedEffect
        vm.applyFilters(appliedFilter)
        onAppliedFilterConsumed()
    }

    DisposableEffect(lifecycleOwner, vm) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> vm.onStart()
                Lifecycle.Event.ON_STOP -> vm.onStop()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ProductsContent(
        uiState = uiState,
        onRetry = vm::retryProductsLoading,
        onRefresh = vm::refreshProducts,
        onFiltersClick = vm::onFiltersClick,
        onFilterRemove = vm::onSelectedFilterRemove,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsContent(
    uiState: UiState,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onFiltersClick: () -> Unit,
    onFilterRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        when {
            uiState.products.isLoading && uiState.products.products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.products.error != null && uiState.products.products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Что-то пошлло не так, попробуйте снова",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onRetry) {
                            Text(text = "Перезагрузить")
                        }
                    }
                }
            }

            else -> {
                Text(
                    text = uiState.header.text,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterActionChip(
                            title = "Фильтры",
                            hasSelectedFilters = uiState.header.selectedFilters.isNotEmpty(),
                            onClick = onFiltersClick
                        )
                    }

                    items(uiState.header.selectedFilters, key = { it.filterId }) { chip ->
                        SelectedFilterChip(
                            title = chip.title,
                            onRemove = { onFilterRemove(chip.filterId) }
                        )
                    }
                }

                PullToRefreshBox(
                    isRefreshing = uiState.products.isLoading && uiState.products.products.isNotEmpty(),
                    onRefresh = onRefresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        gridItems(
                            items = uiState.products.products,
                            key = { item -> item.id }
                        ) { product ->
                            ProductCard(product = product)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterActionChip(
    title: String,
    hasSelectedFilters: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE6EEF9))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                id = if (hasSelectedFilters) {
                    R.drawable.ic_other_filter_selected_pseudo24
                } else {
                    R.drawable.ic_other_filter_unselected_pseudo24
                }
            ),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            color = Color(0xFF111111),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun SelectedFilterChip(
    title: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1677E6))
            .padding(start = 12.dp, end = 6.dp, top = 8.dp, bottom = 8.dp)
            .wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp),
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_24_cross_white),
                    contentDescription = "Удалить фильтр",
                    tint = Color.Unspecified
                )
            }
        )
    }
}

@Composable
private fun ProductCard(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            onError = { error ->
                Log.e(
                    "CoilProductImage",
                    "Failed to load ${product.imageUrl}",
                    error.result.throwable
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Text(
            text = formatPrice(product.price),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun formatPrice(price: Int): String {
    val groupSeparator = DecimalFormatSymbols().groupingSeparator
    val raw =
        price.toString().reversed().chunked(3).joinToString(groupSeparator.toString()).reversed()
    return "$raw ₽"
}
