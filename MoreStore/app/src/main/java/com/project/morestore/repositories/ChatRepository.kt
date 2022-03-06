package com.project.morestore.repositories

import android.util.Log
import com.project.morestore.models.CreatedDialogId
import com.project.morestore.models.CreatingDialog
import com.project.morestore.models.DialogWrapper
import com.project.morestore.singletones.CreateProductData
import com.project.morestore.singletones.Network
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class ChatRepository {
    private val chatApi = Network.chatApi


    suspend fun createDialog(userId: Long, productId: Long): Response<CreatedDialogId>?{
        return try {
            chatApi.createDialog(CreatingDialog(userId, productId))
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = chatApi.createDialogGetError(CreatingDialog(userId, productId))
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

    suspend fun getDialogById(id: Long): Response<DialogWrapper>?{
        return try {
            chatApi.getDialogById(id)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = chatApi.getDialogByIdGetError(id)
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


}