package ru.detmir.blocksexample.products.domain.model

data class ProductCatalogResult(
    val products: List<Product>,
    val availableFilters: List<ProductAvailableFilter>
)
