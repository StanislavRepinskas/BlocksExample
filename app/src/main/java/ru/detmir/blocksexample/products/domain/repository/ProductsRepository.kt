package ru.detmir.blocksexample.products.domain.repository

import ru.detmir.blocksexample.products.domain.model.Product

interface ProductsRepository {
    suspend fun getProducts(): List<Product>
}
