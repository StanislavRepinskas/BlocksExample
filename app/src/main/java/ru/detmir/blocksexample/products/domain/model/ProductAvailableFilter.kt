package ru.detmir.blocksexample.products.domain.model

data class ProductAvailableFilter(
    val filterId: String,
    val title: String,
    val type: Type,
    val values: List<FilterValue>
) {
    enum class Type {
        Single,
        Multiple
    }
}
