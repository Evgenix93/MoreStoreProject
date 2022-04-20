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
    suspend fun getUserInfoById(@Path("id") id: Long): Response<User>

    @GET("user/{id}")
    suspend fun getUserInfoByIdGetError(@Path("id") id: Long): Response<String>

    @POST("wishlist")
    suspend fun addProductToWishList(@Body wishList: BrandWishList): Response<List<Long>>

    @POST("wishlist")
    suspend fun addProductToWishListGetError(@Body wishList: BrandWishList): Response<String>

    @GET("wishlist")
    suspend fun getProductWishList(): Response<List<Product>>

    @GET("wishlist")
    suspend fun getProductWishListGetError(): Response<String>

    @POST("property/save")
    suspend fun saveBrandsProperties(@Body brandsPropertiesData: BrandsPropertiesData): Response<Boolean>

    @GET("property/check_save")
    suspend fun loadBrandsProperties(@Query("id_user") idUser: Long): Response<List<BrandsPropertiesDataWrapper>>

    @GET("wishlist/user")
    suspend fun getSellersWishList(): Response<List<User>>

    @GET("wishlist/user")
    suspend fun getSellersWishListGetError(): Response<String>

    @POST("wishlist/user")
    suspend fun addDeleteSellerToWishList(@Body wishlist: BrandWishList): Response<List<Long>>

    @POST("wishlist/user")
    suspend fun addDeleteSellerToWishListGetError(@Body wishlist: BrandWishList): Response<String>

    @GET("save_search")
    suspend fun getFavoriteSearches(): Response<List<FavoriteSearch>>

    @GET("save_search")
    suspend fun getFavoriteSearchesGetError(): Response<String>

    @GET("save_search/{id}")
    suspend fun getFavoriteSearchById(@Path("id") id: Long): Response<FavoriteSearch>

    @GET("save_search/{id}")
    suspend fun getFavoriteSearchByIdGetError(@Path("id") id: Long): Response<String>

    @POST("save_search")
    suspend fun saveFavoriteSearch(@Body favoriteSearch: FavoriteSearchValue): Response<Id>

    @POST("save_search")
    suspend fun saveFavoriteSearchGetError(@Body favoriteSearch: FavoriteSearchValue): Response<String>

    @POST("save_search/put")
    suspend fun editFavoriteSearch(@Body favoriteSearch: FavoriteSearchValue): Response<FavoriteSearchValue>

    @POST("save_search/put")
    suspend fun editFavoriteSearchGetError(@Body favoriteSearch: FavoriteSearchValue): Response<String>

    @POST("save_search/delete")
    suspend fun deleteFavoriteSearch(@Body id: Id): Response<Boolean>

    @POST("save_search/delete")
    suspend fun deleteFavoriteSearchGetError(@Body id: Id): Response<String>









}