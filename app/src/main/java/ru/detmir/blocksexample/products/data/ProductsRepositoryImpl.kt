package ru.detmir.blocksexample.products.data

import kotlinx.coroutines.delay
import javax.inject.Inject
import ru.detmir.blocksexample.products.domain.model.FilterValue
import ru.detmir.blocksexample.products.domain.model.Product
import ru.detmir.blocksexample.products.domain.model.ProductAvailableFilter
import ru.detmir.blocksexample.products.domain.model.ProductCatalogResult
import ru.detmir.blocksexample.products.domain.model.ProductFilter
import ru.detmir.blocksexample.products.domain.repository.ProductsRepository

class ProductsRepositoryImpl @Inject constructor() : ProductsRepository {

    override suspend fun getProducts(
        filters: ProductFilter,
        page: Int
    ): ProductCatalogResult {
        delay(1_000)

        val imageUrls = listOf(
            "https://img.detmir.st/U9jF4vbmNRHuLeK1OZfdCYt70fnlf_tM5Ca3m96t4OQ/rs:fit:500:625/g:sm/ex:1/bg:FFFFFF/aHR0cHM6Ly9jYXRhbG9nLWNkbi5kZXRtaXIuc3QvbWVkaWEveVVDS3pSOHlmSFFyQm9XVkdsY3NtR203bHJzUUFJblVQVHRfd1NhZHJVbz0uanBlZw.webp",
            "https://img.detmir.st/LhMWegWnVXwH9F0cUhe7q4W4PAWj_fkPJbKVPNr3xN0/rs:fit:500:625/g:sm/ex:1/bg:FFFFFF/aHR0cHM6Ly9jYXRhbG9nLWNkbi5kZXRtaXIuc3QvbWVkaWEvc2owN0k4RDNnYzBKU2daRXpGRm1fVnpxRFZGMkViOGlHR2lfSkoxeXE5MD0uanBlZw.webp",
            "https://img.detmir.st/_P-xlE2d-Thu6esgOhmBPyOxPR1dioQW7A6UiEbdfMg/rs:fit:500:625/g:sm/ex:1/bg:FFFFFF/aHR0cHM6Ly9jYXRhbG9nLWNkbi5kZXRtaXIuc3QvbWVkaWEvOHJkY1hWUFZGWnUxOENCcExPcU9xODU1d21zeURXMUFydlpNNDB3WWN3VT0uanBlZw.webp",
            "https://img.detmir.st/Wqtrrf6hPlyqqSVcgc_ZPGvnCtC71IfYNCkYvVNjlMw/rs:fit:500:625/g:sm/ex:1/bg:FFFFFF/aHR0cHM6Ly9jYXRhbG9nLWNkbi5kZXRtaXIuc3QvbWVkaWEvX09jNEI3VHZFeG5EWjg3Q0hlcEFiNEdBVHIyOWlWRGR3TXVrT1loTTUyQT0uanBlZw.webp"
        )

        val products = List(40) { index ->
            Product(
                id = "product_${index + 1}",
                price = 1200 + (index * 37),
                name = "Товар ${index + 1}",
                imageUrl = imageUrls[index % imageUrls.size]
            )
        }

        val availableFilters = listOf(
            ProductAvailableFilter(
                filterId = "brand",
                title = "Бренд",
                type = ProductAvailableFilter.Type.Single,
                values = listOf(
                    FilterValue(id = "manu", value = "manu", title = "Manu"),
                    FilterValue(id = "huggies", value = "huggies", title = "Huggies")
                )
            ),
            ProductAvailableFilter(
                filterId = "color",
                title = "Цвет",
                type = ProductAvailableFilter.Type.Multiple,
                values = listOf(
                    FilterValue(id = "red", value = "red", title = "Красный"),
                    FilterValue(id = "blue", value = "blue", title = "Синий")
                )
            )
        )

        return ProductCatalogResult(
            products = products,
            availableFilters = availableFilters
        )
    }
}
