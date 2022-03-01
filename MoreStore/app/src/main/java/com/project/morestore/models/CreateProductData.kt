package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass (generateAdapter = true)
data class CreateProductData(
    @Json(name = "id_category")
    var idCategory: Int? = null,
    @Json(name = "id_brand")
    var idBrand: Long? = null,
    var address: String? = null,
    var date: Long? = null,
    @Json(name = "date_end")
    var dateEnd: Long? = null,
    var status: Int? = null,
    var price: String? = null,
    var sale: Int? = null,
    var name: String? = null,
    var about: String? = null,
    var phone: String? = null,
    var property: MutableList<Property2>? = null,
    var id: Long? = null
)
