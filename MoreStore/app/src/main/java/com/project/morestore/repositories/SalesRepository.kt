package com.project.morestore.repositories

import android.util.Log
import com.project.morestore.apis.SalesApi
import com.project.morestore.models.DealPlace
import com.project.morestore.models.Order
import com.project.morestore.models.OrderPlace
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.singletones.Network
import com.squareup.moshi.JsonDataException
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class SalesRepository @Inject constructor(private val salesApi: SalesApi) {

    suspend fun getSales(): Response<List<Order>>? {
       return try {
           salesApi.getSales()
       }catch(e: Throwable){
           Log.e("MyDebug", "error = ${e.message}")
           if(e is IOException)
               null
           else if(e is JsonDataException)
               Response.error(400, "Json ошибка".toResponseBody())
           else try{
              val error = salesApi.getSalesErrorString()
              Response.error(400, error.toResponseBody())
           }catch(e: Throwable){
               Response.error(400, "Ошибка".toResponseBody())
           }
       }
    }

   suspend fun addDealPlace(orderId: Long, address: String): Response<Boolean>?{
       return try{
          salesApi.addDealPlace(DealPlace(orderId, address))
       }catch (e: Throwable){
           Log.e("MyDebug", "error = ${e.message}")
           null
       }
   }

}