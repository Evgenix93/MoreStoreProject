package com.project.morestore.repositories

import android.content.Context
import android.util.Log
import com.project.morestore.models.*

import com.project.morestore.singletones.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class ProductRepository(private val context: Context) {
    private val onBoardingApi = Network.onBoardingApi
    private val productApi = Network.productApi

    suspend fun getAllSizes(): Response<List<Size>>?{
        return try {
            onBoardingApi.getAllSizes()
        }catch (e: Throwable){
            if(e is IOException){
                null
            }else{
                try {
                    val response = onBoardingApi.getAllSizesGetError()
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }}}



    suspend fun getCategories(): Response<List<Category>>? {
        return try {
            onBoardingApi.getCategories()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = onBoardingApi.getCategoriesGetError()
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getProducts(filter: List<String>): Response<List<Product>>?{
        return try {
            productApi.getProducts(PRODUCT_OPTIONS, filter.joinToString(";"))
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getProductsGetError(PRODUCT_OPTIONS, filter.joinToString(";"))
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(404,   "не найдено".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getYouMayLikeProducts(limit: Int, userId: Int): Response<List<Product>>?{
        return try {
            productApi.getYouMayLikeProducts(limit, userId)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getYouMayLikeProductsGetError(limit, userId)
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getSearchSuggestions(query: String): Response<List<String>>?{
        return try {
            productApi.getSearchSuggestions(query)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getSearchSuggestionsGetError(query)
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getCities(): Response<List<Region>>?{
        return try {
            productApi.getCities()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getCitiesGetError()
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getBrands(): Response<List<ProductBrand>>?{
        return try {
            productApi.getAllBrands()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getAllBrandsGetError()
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }



    suspend fun saveSizes(topSizesList: List<Size>, bottomSizesList: List<Size>, shoesSizesList: List<Size>): Boolean{
        return try {
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
        }

    }



        suspend fun safeCategories(categoryIdList: List<Int>): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val sharedPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                val categoryIdStringSet = categoryIdList.map { it.toString() }.toMutableSet()
                sharedPrefs.edit().apply {
                    clear()
                    putStringSet(CATEGORIES, categoryIdStringSet)
                }.commit()
            }
        } catch (e: Throwable) {
            false
        }
    }

    suspend fun loadSizes(): List<MutableSet<String>?>{
        return withContext(Dispatchers.IO){
            val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            val set = prefs.getStringSet(TOP_SIZES_KEY, null)
            val set2 = prefs.getStringSet(BOTTOM_SIZES_KEY, null)
            val set3 = prefs.getStringSet(SHOES_SIZES_KEY, null)
            listOf(set,set2,set3)
        }
    }

    suspend fun loadCategories(): MutableSet<String>?{
        return withContext(Dispatchers.IO){
            val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            prefs.getStringSet(CATEGORIES, null)
        }
    }

    suspend fun saveOnBoardingViewed(): Boolean{
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(ONBOARDINGVIEWED, true).commit()

            }
        }catch (e: Throwable){
            false
        }
    }

    suspend fun loadOnBoardingViewed(): Boolean{
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                prefs.getBoolean(ONBOARDINGVIEWED, false)

            }
        }catch (e: Throwable){
            false
        }

    }

    companion object {
        const val USER_PREFS = "user_prefs"
        const val TOP_SIZES_KEY = "top_sizes_key"
        const val BOTTOM_SIZES_KEY = "bottom_sizes_key"
        const val SHOES_SIZES_KEY = "shoes_sizes_key"
        const val CATEGORIES = "categoryIdList"
        const val ONBOARDINGVIEWED = "viewedOnBoarding"
        const val PRODUCT_OPTIONS = "service,user,category,property,statistics,brand,category,property_open_category"
    }
}