package com.project.morestore.repositories

import android.util.Log
import com.project.morestore.models.DealPlace
import com.project.morestore.models.Order
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.singletones.Network
import com.squareup.moshi.JsonDataException
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class SalesRepository {

    suspend fun getSales(): Response<List<Order>>? {
       return try {
           Network.salesApi.getSales()
       }catch(e: Throwable){
           Log.e("MyDebug", "error = ${e.message}")
           if(e is IOException)
               null
           else if(e is JsonDataException)
               Response.error(400, "Json ошибка".toResponseBody())
           else try{
              val error = Network.salesApi.getSalesErrorString()
              Response.error(400, error.toResponseBody())
           }catch(e: Throwable){
               Response.error(400, "Ошибка".toResponseBody())
           }
       }
    }

   suspend fun addDealPlace(orderId: Long, address: String): Response<Boolean>?{
       return try{
          Network.salesApi.addDealPlace(DealPlace(orderId, address))
       }catch (e: Throwable){
           Log.e("MyDebug", "error = ${e.message}")
           null
       }
   }
}