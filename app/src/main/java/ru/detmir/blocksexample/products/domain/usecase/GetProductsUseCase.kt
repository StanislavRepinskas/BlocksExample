package ru.detmir.blocksexample.products.domain.usecase

import javax.inject.Inject
import ru.detmir.blocksexample.products.domain.model.ProductCatalogResult
import ru.detmir.blocksexample.products.domain.model.ProductFilter
import ru.detmir.blocksexample.products.domain.repository.ProductsRepository

class GetProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository
) {
    suspend operator fun invoke(
        filters: ProductFilter,
        page: Int = 0
    ): ProductCatalogResult {
        return productsRepository.getProducts(filters = filters, page = page)
    }
}
