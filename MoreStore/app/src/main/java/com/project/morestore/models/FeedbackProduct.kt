package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class FeedbackProduct(
    val id: Long,
    @Json(name = "photo")
    val photos :Array<ProductPhoto>?,
    @Json(name = "name")
    val title :String,
    val brand :ProductBrand?,
    val price :String,
    @Json(name = "price_new")
    val newPrice: String?,
    val sale :Float,
    val category: Category?,
    val property: Array<Property>?,
    @Json(name = "package")
    val packageDimensions: ProductDimensions
){
   // val newPrice get() = price.toFloat()
   // val oldPrice = newPrice / (sale/100)
    val state :String? get() = property?.find{ it.id == 11L }?.value
}