package com.project.morestore.util

enum class SortingType(val value: String) {
    NEW("date"),
    CHEAP("price_start"),
    EXPENSIVE("price_end"),
    POPULAR("Популярные")
}