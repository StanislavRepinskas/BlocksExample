package ru.detmir.blocksexample.products.domain.usecase

import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository
) {
    suspend operator fun invoke(): List<Product> {
        return productsRepository.getProducts()
    }
}

interface ProductsRepository {
    suspend fun getProducts(): List<Product>
}

class ProductsRepositoryImpl: ProductsRepository {
    override suspend fun getProducts(): List<Product> {
        return emptyList()
    }
}