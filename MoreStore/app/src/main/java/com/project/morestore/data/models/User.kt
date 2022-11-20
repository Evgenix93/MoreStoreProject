package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class User(
    val id: Long,
    val name: String?,
    val phone: String?,
    val email: String?,
    val surname: String?,
    val avatar: UserAvatar?,
    @Json(name = "created_at")
    val createdAt: String?,
    val rating: SellerRating?,
    @Json(name = "is_blacklist")
    val isBlackList: Boolean?
): Parcelable{
    override fun equals(other: Any?): Boolean {
        val otherUser = other as User?
        return id == otherUser?.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
