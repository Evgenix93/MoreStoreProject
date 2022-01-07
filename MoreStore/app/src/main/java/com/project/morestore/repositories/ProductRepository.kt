package com.project.morestore.repositories

import android.content.Context
import com.project.morestore.models.Category
import com.project.morestore.singletones.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class ProductRepository(private val context: Context) {
    suspend fun getCategories(): Response<List<Category>>? {
        return try {
            Network.onboardingApi.getCategories()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Response.error(400, "".toResponseBody(null))
            }
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
}