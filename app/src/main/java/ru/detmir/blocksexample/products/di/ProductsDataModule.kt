package ru.detmir.blocksexample.products.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.detmir.blocksexample.products.data.ProductsRepositoryImpl
import ru.detmir.blocksexample.products.domain.repository.ProductsRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsDataModule {

    @Binds
    @Singleton
    abstract fun bindProductsRepository(
        impl: ProductsRepositoryImpl
    ): ProductsRepository
}
