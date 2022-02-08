package com.project.morestore.repositories

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.project.morestore.models.*

import com.project.morestore.singletones.FilterState

import com.project.morestore.singletones.Network
import com.project.morestore.singletones.Token
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class ProductRepository(private val context: Context) {
    private val onBoardingApi = Network.onBoardingApi
    private val productApi = Network.productApi

    suspend fun getAllSizes(): Response<List<Size>>? {
        return try {
            onBoardingApi.getAllSizes()
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = onBoardingApi.getAllSizesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }


    suspend fun getCategories(): Response<List<Category>>? {
        return try {
            onBoardingApi.getCategories()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = onBoardingApi.getCategoriesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getProducts(
        query: String? = null,
        filter: Filter? = null,
        userId: Long? = null,
        productId: Long? = null
    ): Response<List<Product>>? {
        return try {
            var categoryStr = listOf<String>()
            var brandsStr = listOf<String>()
            var citiesStr = listOf<String>()
            var queryStr = listOf<String>()
            var productIdStr = listOf<String>()
            var productPropertyStr = listOf<String>()

            if (filter?.categories?.isNotEmpty() == true) {
                categoryStr =
                    filter.categories.filter { it.isChecked == true }.map { "id_category=${it.id}" }
            }
            if (filter?.brands?.isNotEmpty() == true) {
                brandsStr =
                    filter.brands.filter { it.isChecked == true }.map { "id_brand=${it.id}" }
            }
            if (filter?.regions?.isNotEmpty() == true) {
                citiesStr =
                    filter.regions.filter { it.isChecked == true }.map { "id_city=${it.id}" }
            }



            if (filter?.isCurrentLocationFirstLoaded == false && filter.currentLocation != null) {
                Log.d("mylog", "load current location")
                citiesStr = citiesStr + listOf("id_city=${filter.currentLocation?.id}")
                Log.d("mylog", "citystr = $citiesStr")
            }

            if (!query.isNullOrEmpty()) {
                queryStr = listOf("text=${query.orEmpty()}")
            }

            productId?.let {
                productIdStr = listOf("id=$it")
            }

            productPropertyStr = filter?.chosenTopSizes?.map { "id_property=${it.id}" }
                .orEmpty() + filter?.chosenBottomSizes?.map { "id_property=${it.id}" }
                .orEmpty() + filter?.chosenShoosSizes?.map { "id_property=${it.id}" }
                .orEmpty() + filter?.chosenTopSizesMen?.map { "id_property=${it.id}" }
                .orEmpty() + filter?.chosenBottomSizesMen?.map { "id_property=${it.id}" }
                .orEmpty() + filter?.chosenShoosSizesMen?.map { "id_property=${it.id}" }
                .orEmpty() + filter?.chosenTopSizesKids?.map { "id_property=${it.id}" }
                .orEmpty() + filter?.chosenBottomSizesKids?.map { "id_property=${it.id}" }
                .orEmpty() + filter?.chosenShoosSizesKids?.map { "id_property=${it.id}" }
                .orEmpty() + filter?.chosenMaterials?.map { "id_property=${it.id}" }.orEmpty()

            productApi.getProducts(
                PRODUCT_OPTIONS,
                (categoryStr + brandsStr + citiesStr + queryStr + productIdStr + productPropertyStr).joinToString(
                    ";"
                ),
                userId
            )

        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getProductsGetError(
                        PRODUCT_OPTIONS,
                        listOf<String>().joinToString(";"),
                        userId
                    )
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(404, "не найдено".toResponseBody(null))
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getCurrentUserProducts(): Response<List<Product>>? {
        return getProducts(userId = Token.userId.toLong())
    }

    suspend fun getYouMayLikeProducts(limit: Int, userId: Int): Response<List<Product>>? {
        return try {
            productApi.getYouMayLikeProducts(limit, userId)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getYouMayLikeProductsGetError(limit, userId)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getSearchSuggestions(query: String): Response<List<String>>? {
        return try {
            productApi.getSearchSuggestions(query)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getSearchSuggestionsGetError(query)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getCities(): Response<List<Region>>? {
        return try {
            productApi.getCities()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getCitiesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getBrands(): Response<List<ProductBrand>>? {
        return try {
            productApi.getAllBrands()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getAllBrandsGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getProperties(): Response<List<Property>>? {
        return try {
            productApi.getProperties()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getPropertiesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }


    fun saveSizes(
        topSizesList: List<Size>,
        bottomSizesList: List<Size>,
        shoesSizesList: List<Size>
    ) {
        // return try {
        //suspend fun saveSizes(topSizesList: List<Size>, bottomSizesList: List<Size>, shoesSizesList: List<Size>){
        /* return try {
             withContext(Dispatchers.IO){
                 //error("error")
                 val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                 val stringSet = mutableSetOf<String>()
                 val stringSet2 = mutableSetOf<String>()
                 val stringSet3 = mutableSetOf<String>()

                 for(size in topSizesList){
                     stringSet.add(size.id.toString())
                 }
                 for(size in bottomSizesList){
                     stringSet2.add(size.id.toString())
                 }
                 for(size in shoesSizesList){
                     stringSet3.add(size.id.toString())
                 }
                 prefs.edit().apply {
                     putStringSet(TOP_SIZES_KEY, stringSet )
                     putStringSet(BOTTOM_SIZES_KEY, stringSet2)
                     putStringSet(SHOES_SIZES_KEY, stringSet3)
                 }.commit()

             }
         }catch (e: Throwable){
             false
         }*/
        val sizeList = listOf(
            SizeLine(
                0,
                "XXS",
                "26-27",
                "42",
                "32",
                "32",
                false
            ),
            SizeLine(
                0,
                "XS",
                "28-29",
                "44",
                "34",
                "34",
                false
            ),
            SizeLine(
                0,
                "S",
                "30-31",
                "46",
                "36",
                "36",
                false
            ),
            SizeLine(
                0,
                "M",
                "32-33",
                "48",
                "38",
                "38",
                false
            ),
            SizeLine(
                0,
                "L",
                "34-35",
                "50",
                "40",
                "40",
                false
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false
            ),
            com.project.morestore.models.SizeLine(
                0,
                "",
                "",
                "",
                "",
                "",
                false
            )
        )
        topSizesList.forEachIndexed { index, size ->
            if (sizeList[index].int == size.name)
                sizeList[index].isSelected = size.chosen ?: false
            sizeList[index].id = size.id
        }
        FilterState.filter.chosenTopSizes = sizeList
    }


    fun safeCategories(segmentsChecked: List<Boolean>) {
        FilterState.filter.segments = segmentsChecked
        FilterState.filter.isAllBrands = segmentsChecked.all { !it }
    }


    suspend fun loadSizes(): List<MutableSet<String>?> {
        return withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            val set = prefs.getStringSet(TOP_SIZES_KEY, null)
            val set2 = prefs.getStringSet(BOTTOM_SIZES_KEY, null)
            val set3 = prefs.getStringSet(SHOES_SIZES_KEY, null)
            listOf(set, set2, set3)
        }
    }

    suspend fun loadCategories(): MutableSet<String>? {
        return withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            prefs.getStringSet(CATEGORIES, null)
        }
    }

    suspend fun saveOnBoardingViewed(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(ONBOARDINGVIEWED, true).commit()

            }
        } catch (e: Throwable) {
            false
        }
    }

    suspend fun loadOnBoardingViewed(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                prefs.getBoolean(ONBOARDINGVIEWED, false)

            }
        } catch (e: Throwable) {
            false
        }

    }

    suspend fun getProductCategoriesAdults(): Response<List<ProductCategoryAdults>>? {
        return try {
            Network.productApi.getProductCategoriesAdults()
        } catch (e: Throwable) {
            Log.e("Debug", e.message.orEmpty(), e)
            if (e is IOException)
                null
            else
                Response.error(400, "".toResponseBody())
        }
    }

    suspend fun getProductCategoriesKids(): Response<List<ProductCategoryKids1>>? {
        return try {
            Network.productApi.getProductCategoriesKids()
        } catch (e: Throwable) {
            Log.e("Debug", e.message.orEmpty(), e)
            if (e is IOException)
                null
            else
                Response.error(400, "".toResponseBody())
        }
    }

    fun getShareProductIntent(id: Long): Intent {
        val uri = Uri.withAppendedPath(Uri.parse("https://morestore.ru/products/"), id.toString())
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, uri.toString())
            type = "text/plain"
        }
    }


    fun loadForWho(): List<Boolean> {
        return FilterState.filter.chosenForWho
    }

    companion object {
        const val USER_PREFS = "user_prefs"
        const val TOP_SIZES_KEY = "top_sizes_key"
        const val BOTTOM_SIZES_KEY = "bottom_sizes_key"
        const val SHOES_SIZES_KEY = "shoes_sizes_key"
        const val CATEGORIES = "categoryIdList"
        const val ONBOARDINGVIEWED = "viewedOnBoarding"
        const val PRODUCT_OPTIONS =
            "service,user,category,property,statistics,brand,category,property_open_category"
        const val FILTER_KEY = "filter"
    }
}