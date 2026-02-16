package ru.detmir.blocksexample.products.domain.model

class ProductFilter(
    private val valuesByFilterId: MutableMap<String, MutableSet<FilterValue>> = mutableMapOf()
) {

    fun addFilterValue(filterId: String, values: Set<FilterValue>) {
        if (values.isEmpty()) {
            valuesByFilterId.remove(filterId)
            return
        }
        valuesByFilterId[filterId] = values.toMutableSet()
    }

    fun removeFilterValue(filterId: String) {
        valuesByFilterId.remove(filterId)
    }

    fun removeFilterValue(filterId: String, valueId: String) {
        val values = valuesByFilterId[filterId] ?: return
        values.removeAll { it.id == valueId }
        if (values.isEmpty()) {
            valuesByFilterId.remove(filterId)
        }
    }

    fun getFilterValue(filterId: String): Set<String> {
        return valuesByFilterId[filterId]
            ?.map { it.id }
            ?.toSet()
            ?: emptySet()
    }

    fun getSelectedValues(): Map<String, Set<FilterValue>> {
        return valuesByFilterId.mapValues { (_, values) -> values.toSet() }
    }

    fun copy(): ProductFilter {
        return ProductFilter(
            valuesByFilterId = valuesByFilterId
                .mapValues { (_, values) -> values.toMutableSet() }
                .toMutableMap()
        )
    }

    companion object {
        fun initialWithCategoryShoes(): ProductFilter {
            return ProductFilter().apply {
                addFilterValue(
                    filterId = "category",
                    values = setOf(
                        FilterValue(
                            id = "shoes",
                            value = "shoes",
                            title = "Обувь"
                        )
                    )
                )
            }
        }
    }
}

data class FilterValue(
    val id: String,
    val value: String,
    val title: String
)
