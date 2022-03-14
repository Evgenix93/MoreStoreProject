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






}