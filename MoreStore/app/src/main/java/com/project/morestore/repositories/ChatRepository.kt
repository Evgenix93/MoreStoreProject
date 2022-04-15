package com.project.morestore.repositories

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.project.morestore.models.*
import com.project.morestore.singletones.ChatMedia
import com.project.morestore.singletones.CreateProductData
import com.project.morestore.singletones.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class ChatRepository(val context: Context) {
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

    suspend fun addMessage(text: String, dialogId: Long): Response<MessageModel>? {
        return try {
            chatApi.addMessage(CreatingMessage(dialogId, text))
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = chatApi.addMessageGetError(CreatingMessage(dialogId, text))
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

    suspend fun getDialogs(): Response<List<DialogWrapper>>? {
        return try {
            chatApi.getDialogs()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = chatApi.getDialogsGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: e.message.toString().toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, e.message.toString().toResponseBody(null))
                }
            }
        }
    }

    suspend fun deleteDialog(dialogId: Long): Response<DialogId>?{
        return try {
            chatApi.deleteDialog(DialogId(dialogId))
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = chatApi.deleteDialogGetError(DialogId(dialogId))
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

    suspend fun uploadVideos(uris: List<Uri>, messageId: Long): Response<List<ProductVideo>>?{
        return withContext(Dispatchers.IO) {

            val videos = uris.mapNotNull {
                if(context.contentResolver.getType(it)?.substringAfter('/') == "mp4")
                PhotoVideo(
                    type = "mp4",
                    base64 = context.contentResolver.openInputStream(it).use {
                        Base64.encodeToString(it?.readBytes(), Base64.DEFAULT)
                    }
                )else
                    null
            }
            if(videos.isEmpty())
                 return@withContext null
            val videoData = VideoData(
                type = "Message",
                idType = messageId,
                videos
            )
             try {
                chatApi.uploadVideo(videoData)
            } catch (e: Exception) {
                if (e is IOException) {
                    null
                } else {
                    Log.d("mylog", e.message.toString())
                    try {
                        val response = chatApi.uploadVideoGetError(videoData)
                        if (response.code() == 500) {
                            Response.error(500, "".toResponseBody(null))
                        } else {
                            Response.error(
                                400,
                                response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(
                                    null
                                )
                            )
                        }
                    } catch (e: Throwable) {
                        Response.error(400, "ошибка".toResponseBody(null))
                    }
                }
            }
        }
    }

    suspend fun uploadPhotos(uris: List<Uri>, messageId: Long): Response<List<ProductPhoto>>?{
        return withContext(Dispatchers.IO) {
            val photos = uris.mapNotNull {
                if(context.contentResolver.getType(it)?.substringAfter('/') != "mp4")
                PhotoVideo(
                    type = context.contentResolver.getType(it)?.substringAfter('/') ?: "jpg",
                    base64 = context.contentResolver.openInputStream(it).use {inputStream ->
                        Base64.encodeToString(inputStream?.readBytes(), Base64.DEFAULT)
                    }
                )else
                    null
            }
            if(photos.isEmpty())
                return@withContext null
            val photoData = PhotoData(
                type = "MessagePhoto",
                idType = messageId,
                photos
            )
            try {
                chatApi.uploadPhoto(photoData)
            } catch (e: Exception) {
                if (e is IOException) {
                    null
                } else {
                    Log.d("mylog", e.message.toString())
                    try {
                        val response = chatApi.uploadPhotoGetError(photoData)
                        if (response.code() == 500) {
                            Response.error(500, "".toResponseBody(null))
                        } else {
                            Response.error(
                                400,
                                response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(
                                    null
                                )
                            )
                        }
                    } catch (e: Throwable) {
                        Response.error(400, "ошибка".toResponseBody(null))
                    }
                }
            }
        }
    }

    fun loadMediaUris() = ChatMedia.mediaUris

    fun clearMediaUris(){
        ChatMedia.mediaUris = null
    }

   suspend fun offerDiscount(info: ChatFunctionInfo): Response<ChatFunctionInfo>?{
     return try {
         chatApi.offerDiscount(info)
     }catch(e: Throwable){
         Log.e("MyDebug", "error = ${e.message}")
         null
       }
    }

    suspend fun submitDiscount(info: ChatFunctionInfo): Response<ChatFunctionInfo>?{
        return try {
            chatApi.submitDiscount(info)
        }catch(e: Throwable){
            Log.e("MyDebug", "error = ${e.message}")
            null
        }
    }

    suspend fun submitBuy(info: ChatFunctionInfo): Response<ChatFunctionInfo>?{
        return try{
         chatApi.submitBuy(info)
        }catch (e: Throwable){
            Log.e("MyDebug", "error = ${e.message}")
            null
        }
    }

    suspend fun submitPrice(info: ChatFunctionInfo): Response<ChatFunctionInfo>?{
        return try{
            chatApi.submitPrice(info)
        }catch (e: Throwable){
            Log.e("MyDebug", "error = ${e.message}")
            null
        }
    }

    suspend fun cancelPrice(info: ChatFunctionInfo): Response<ChatFunctionInfo>?{
        return try{
            chatApi.cancelPrice(info)
        }catch (e: Throwable){
            Log.e("MyDebug", "error = ${e.message}")
            null
        }
    }
}