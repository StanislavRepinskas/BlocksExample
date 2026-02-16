package ru.detmir.blocksexample.products.domain.model

import java.io.Serializable

data class ProductAvailableFilter(
    val filterId: String,
    val title: String,
    val type: Type,
    val values: List<FilterValue>
) : Serializable {
    enum class Type {
        Single,
        Multiple
    }
}
