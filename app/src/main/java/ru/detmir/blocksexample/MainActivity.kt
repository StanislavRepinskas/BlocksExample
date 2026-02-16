package ru.detmir.blocksexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.detmir.blocksexample.products.ProductsScreen
import ru.detmir.blocksexample.products.ProductsViewModel
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
                        composable(route = ROUTE_PRODUCTS) {
                            ProductsScreen(
                                onOpenFilters = { navController.navigate(ROUTE_FILTERS) }
                            )
                        }

                        composable(route = ROUTE_FILTERS) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(ROUTE_PRODUCTS)
                            }
                            val vm: ProductsViewModel = hiltViewModel(parentEntry)
                            val uiState by vm.uiState.collectAsState()

                            FiltersScreen(
                                availableFilters = uiState.availableFilters,
                                initialFilter = uiState.products.selectedFilter,
                                onApply = { filters ->
                                    vm.applyFilters(filters)
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
