package com.project.morestore.repositories

import android.content.Context

import com.project.morestore.models.CommonEntity
import com.project.morestore.models.Size

import com.project.morestore.models.Category

import com.project.morestore.singletones.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class ProductRepository(private val context: Context) {
    private val onBoardingApi = Network.onBoardingApi

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

    suspend fun saveSizes(topSizesList: List<Size>, bottomSizesList: List<Size>, shoesSizesList: List<Size>): Boolean{
        return try {
            withContext(Dispatchers.IO){
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
                val sharedPrefs = context.getSharedPreferences("categories", Context.MODE_PRIVATE)
                val categoryIdStringSet = categoryIdList.map { it.toString() }.toMutableSet()
                sharedPrefs.edit().apply {
                    clear()
                    putStringSet("categoryIdList", categoryIdStringSet)
                }.commit()
            }
        } catch (e: Throwable) {
            false
        }
    }

    companion object {
        const val USER_PREFS = "user_prefs"
        const val TOP_SIZES_KEY = "top_sizes_key"
        const val BOTTOM_SIZES_KEY = "bottom_sizes_key"
        const val SHOES_SIZES_KEY = "shoes_sizes_key"
    }
}