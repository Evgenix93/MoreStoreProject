package com.project.morestore.apis

import com.project.morestore.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageApi {

    @POST("message/dialog/new")
    suspend fun createDialog(@Body dialog: CreatingDialog): Response<CreatedDialogId>

    @POST("message/dialog/new")
    suspend fun createDialogGetError(@Body dialog: CreatingDialog): Response<String>

    @GET("message/dialogs/{id}")
    suspend fun getDialogById(@Path("id") id: Long): Response<DialogWrapper>

    @GET("message/dialogs/{id}")
    suspend fun getDialogByIdGetError(@Path("id") id: Long): Response<String>

    @POST("message")
    suspend fun addMessage(@Body message: CreatingMessage): Response<MessageModel>

    @POST("message")
    suspend fun addMessageGetError(@Body message: CreatingMessage): Response<String>

    @GET("message/dialogs")
    suspend fun getDialogs(): Response<List<DialogWrapper>>

    @GET("message/dialogs")
    suspend fun getDialogsGetError(): Response<String>

    @POST("message/dialog/delete")
    suspend fun deleteDialog(@Body dialogId: DialogId): Response<DialogId>

    @POST("message/dialog/delete")
    suspend fun deleteDialogGetError(@Body dialogId: DialogId): Response<String>

    @POST("upload/video")
    suspend fun uploadVideo(@Body videoData: VideoData): Response<List<ProductVideo>>

    @POST("upload/video")
    suspend fun uploadVideoGetError(@Body videoData: VideoData): Response<String>


    @POST("upload/photo")
    suspend fun uploadPhoto(@Body photoData: PhotoData): Response<List<ProductPhoto>>

    @POST("upload/photo")
    suspend fun uploadPhotoGetError(@Body photoData: PhotoData): Response<String>

    @POST("message/function/buy/suggest")
    suspend fun sendBuyRequest(@Body info: ChatFunctionInfo): Response<ChatFunctionInfo>

    @POST("message/function/buy/suggest")
    suspend fun sendBuyRequestGetError(@Body info: ChatFunctionInfo): Response<String>

    @POST("message/function/buy/cancel")
    suspend fun cancelBuyRequest(@Body info: ChatFunctionInfo): Response<ChatFunctionInfo>

    @POST("message/function/buy/cancel")
    suspend fun cancelBuyRequestGetError(@Body info: ChatFunctionInfo): Response<String>

    @POST("message/function/price/suggest")
    suspend fun sendPriceSuggest(@Body info: ChatFunctionInfo): Response<ChatFunctionInfo>

    @POST("message/function/price/suggest")
    suspend fun sendPriceSuggestGetError(@Body info: ChatFunctionInfo): Response<String>




    @POST("message/function/sale/suggest")
    suspend fun offerDiscount(@Body info: ChatFunctionInfo): Response<ChatFunctionInfo>

    @POST("message/function/sale/ok")
    suspend fun submitDiscount(@Body info: ChatFunctionInfo): Response<ChatFunctionInfo>

    @POST("message/function/buy/ok")
    suspend fun submitBuy(@Body info: ChatFunctionInfo): Response<ChatFunctionInfo>

    @POST("message/function/price/ok")
    suspend fun submitPrice(@Body info: ChatFunctionInfo): Response<ChatFunctionInfo>

    @POST("message/function/price/cancel")
    suspend fun cancelPrice(@Body info: ChatFunctionInfo): Response<ChatFunctionInfo>

    @POST("message/read")
    suspend fun readMessages(@Body dialogId: Id): Response<Id>

    @POST("message/read")
    suspend fun readMessagesGetError(@Body dialogId: Id): Response<String>

}