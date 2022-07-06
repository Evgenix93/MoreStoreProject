package com.project.morestore.apis

import com.project.morestore.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ProductApi {

    @GET("product")
    suspend fun getProducts(
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("optionally") options: String?,
        @Query("filter") filter: String,
        @Query("user") userId: Long?,
    @Query("sort") sort: String?,
    @Query("status") status: Int?): Response<List<Product>>

    @GET("product/{id}")
    suspend fun getProductById(@Path("id") id: Long, @Query("optionally") options: String?): Response<List<Product>>


    @GET("product")
    suspend fun getProductsGetError(
        @Query("optionally") options: String,
        @Query("filter") filter: String,
        @Query("user") userId: Long?,
        @Query("sort") sort: String?): Response<Unit>

    @GET("product/youmaylike")
    suspend fun getYouMayLikeProducts(@Query("limit") limit: Int, @Query("id_user") userId: Long): Response<List<Product>>

    @GET("product/youmaylike")
    suspend fun getYouMayLikeProductsGetError(@Query("limit") limit: Int, @Query("id_user") userId: Long): Response<String>

    @GET("search/suggestions")
    suspend fun getSearchSuggestions(@Query("text") text: String): Response<List<Suggestion>>

    @GET("search/suggestions")
    suspend fun getSearchSuggestionsGetError(@Query("text") text: String): Response<String>

    @GET("geo/city")
    suspend fun getCities(): Response<List<Region>>

    @GET("geo/city")
    suspend fun getCitiesGetError(): Response<String>

    @GET("brand")
    suspend fun getAllBrands(): Response<List<ProductBrand>>

    @GET("brand")
    suspend fun getAllBrandsGetError(): Response<String>

    @GET("property")
    suspend fun getProperties(): Response<List<Property>>

    @GET("property")
    suspend fun getPropertiesGetError(): Response<String>

    @POST("product")
    suspend fun createProduct(@Body createProductData: CreateProductData): Response<List<CreatedProductId>>

    @POST("product")
    suspend fun createProductGetError(@Body createProductData: CreateProductData): Response<String>


    @POST("brand")
    suspend fun addBrand(@Body brand: NewProductBrand): Response<NewProductBrand>

    @POST("brand")
    suspend fun addBrandGetError(@Body brand: NewProductBrand): Response<String>

    @POST("upload/photo")
    suspend fun uploadProductPhotos(@Body photos: PhotoData): Response<List<ProductPhoto>>

    @POST("upload/photo")
    suspend fun uploadProductPhotosGetError(@Body photos: PhotoData): Response<String>

    @POST("upload/video")
    suspend fun uploadProductVideos(@Body videos: VideoData): Response<List<ProductVideo>>

    @POST("upload/video")
    suspend fun uploadProductVideosGetError(@Body videos: VideoData): Response<String>


    @GET("category/subs?id_category=2")
    suspend fun getProductCategoriesAdults(): Response<List<ProductCategoryAdults>>?

    @GET("category/subs?id_category=1")
    suspend fun getProductCategoriesKids(): Response<List<ProductCategoryKids1>>?

    @GET("category")
    suspend fun getProductCategories(): Response<List<ProductCategory>>

    @POST("product/put_product")
    suspend fun changeProductStatus(@Body deleteData: ChangeStatus): Response<Unit>

    @POST("product/put_product")
    suspend fun changeProduct(@Body productData: CreateProductData): Response<List<CreatedProductId>>

    @POST("product/put_product")
    suspend fun changeProductGetError(@Body productData: CreateProductData): Response<String>

    @POST("upload/photo/background")
    suspend fun deletePhotoBackground(@Body photoData: PhotoData): Response<List<ProductPhoto>>

    @POST("upload/photo/background")
    suspend fun deletePhotoBackgroundGetError(@Body photoData: PhotoData): Response<String>

    @GET()
    suspend fun downloadImage(@Url url: String): ResponseBody

    @POST("statistic/view")
    suspend fun viewProduct(@Body viewProductData: ViewProductData): Response<Boolean>

    @GET("banner")
    suspend fun getBanners(): Response<List<Banner>>

}