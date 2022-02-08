package com.project.morestore.apis

import com.project.morestore.models.*
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("user/put")
    suspend fun changeUserData(
        @Query("email") email: String?,
        @Query("phone") phone: String?,
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("sex") sex: String?,
        @Query("step") step: Int?,
        @Query("code") code: Int?
    ): Response<RegistrationResponse>

    @POST("user/put")
    suspend fun changeUserDataGetError(
        @Query("email") email: String?,
        @Query("phone") phone: String?,
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("sex") sex: String?,
        @Query("step") step: Int?,
        @Query("code") code: Int?
    ): Response<String>



    @POST("user/put")
    suspend fun changeUserData2(
        @Query("email") email: String?,
        @Query("phone") phone: String?,
        @Query("step") step: Int?,
        @Query("code") code: Int?
    ): Response<RegistrationResponse2>

    @POST("user/put")
    suspend fun changeUserData2GetError(
        @Query("email") email: String?,
        @Query("phone") phone: String?,
        @Query("step") step: Int?,
        @Query("code") code: Int?
    ): Response<String>

    @POST("upload/photo")
    suspend fun uploadPhoto(@Body photoData: PhotoData): Response<Unit>

    @POST("upload/photo")
    suspend fun uploadPhotoGetError(@Body photoData: PhotoData): Response<String>

    @GET("geo/geocoder")
    suspend fun getCityByCoords(@Query("coords") coords: String): Response<Address>

    @GET("geo/geocoder")
    suspend fun getCityByCoordsGetError(@Query("coords") coords: String): Response<String>

    @POST("wishlist/brand")
    suspend fun addBrandsToWishList(@Body wishList: BrandWishList): Response<List<Long>>

    @POST("wishlist/brand")
    suspend fun addBrandsToWishListGetError(@Body wishList: BrandWishList): Response<String>

    @GET("wishlist/brand")
    suspend fun getBrandWishList(): Response<List<ProductBrand>>

    @GET("wishlist/brand")
    suspend fun getBrandWishListGetError(): Response<String>

    @GET("user/{id}")
    suspend fun getUserInfoById(@Path("id") id: Int): Response<User>

    @GET("user/{id}")
    suspend fun getUserInfoByIdGetError(@Path("id") id: Int): Response<String>


}