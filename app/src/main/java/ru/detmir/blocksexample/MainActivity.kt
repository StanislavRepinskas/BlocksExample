package ru.detmir.blocksexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.detmir.blocksexample.products.ProductsScreen
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductFilter
import ru.detmir.blocksexample.products.filters.FiltersNavContract
import ru.detmir.blocksexample.products.filters.FiltersScreen
import ru.detmir.blocksexample.ui.theme.BlocksExampleTheme

private const val ROUTE_PRODUCTS = "products"
private const val ROUTE_FILTERS = "filters"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            BlocksExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_PRODUCTS,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable(route = ROUTE_PRODUCTS) { backStackEntry ->
                            val appliedFilter by backStackEntry.savedStateHandle
                                .getStateFlow<ProductFilter?>(FiltersNavContract.RESULT_APPLIED_FILTER, null)
                                .collectAsState()

                            ProductsScreen(
                                onOpenFilters = { availableFilters: List<ProductAvailableFilter>, currentFilter: ProductFilter ->
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(
                                            FiltersNavContract.ARG_AVAILABLE_FILTERS,
                                            ArrayList(availableFilters)
                                        )
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(
                                            FiltersNavContract.ARG_INITIAL_FILTER,
                                            currentFilter.copy()
                                        )
                                    navController.navigate(ROUTE_FILTERS)
                                },
                                pendingAppliedFilter = appliedFilter,
                                onAppliedFilterConsumed = {
                                    backStackEntry.savedStateHandle[FiltersNavContract.RESULT_APPLIED_FILTER] = null
                                }
                            )
                        }

                        composable(route = ROUTE_FILTERS) {
                            FiltersScreen(
                                onApply = { appliedFilter ->
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(FiltersNavContract.RESULT_APPLIED_FILTER, appliedFilter)
                                    navController.popBackStack()
                                },
                                onCancel = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
