package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class AddCartData(
    @Json(name = "id_product")
    var productId: Long,
    @Json(name = "id_user")
    var userId: Long? = null,
    @Json(name = "ip")
    var ip: String? = null,
) : Parcelable
