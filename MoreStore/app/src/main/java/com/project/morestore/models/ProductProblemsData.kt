package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ProductProblemsData(
    @Json(name = "id_product")
    var idProduct: Long?,
    @Json(name = "var_problem")
    var problem: String?,
    @Json(name = "comment")
    var comment: String?,
    @Json(name = "phone")
    var phone: String?,
    @Json(name = "photo")
    var photo: List<PhotoVideo>?
) : Parcelable {
    constructor() : this(
        null,
        null,
        null,
        null,
        null
    )
}
