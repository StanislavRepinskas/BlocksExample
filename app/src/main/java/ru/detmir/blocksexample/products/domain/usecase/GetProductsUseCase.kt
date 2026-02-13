package ru.detmir.blocksexample.products.domain.usecase

import javax.inject.Inject
import ru.detmir.blocksexample.products.domain.model.Product
import ru.detmir.blocksexample.products.domain.repository.ProductsRepository

class GetProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository
) {
    suspend operator fun invoke(): List<Product> {
        return productsRepository.getProducts()
    }
}
