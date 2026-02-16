package ru.detmir.blocksexample.products.domain.repository

import ru.detmir.blocksexample.products.domain.model.ProductCatalogResult
import ru.detmir.blocksexample.products.domain.model.ProductFilter

interface ProductsRepository {
    suspend fun getProducts(
        filters: ProductFilter,
        page: Int
    ): ProductCatalogResult
}
