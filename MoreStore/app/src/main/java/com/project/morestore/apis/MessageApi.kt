package com.project.morestore.apis

import com.project.morestore.models.CreatedDialogId
import com.project.morestore.models.CreatingDialog
import com.project.morestore.models.DialogWrapper
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


}