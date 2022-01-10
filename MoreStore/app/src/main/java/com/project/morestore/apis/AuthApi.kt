package com.project.morestore.apis

import com.project.morestore.models.PhotoData
import com.project.morestore.models.RegistrationData
import com.project.morestore.models.RegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query



interface AuthApi {

    @POST("user/registration")
    suspend fun register(@Body data: RegistrationData): Response<RegistrationResponse>

    @POST("user/registration")
    suspend fun registerGetError(@Body data: RegistrationData): Response<String>




    @POST("user/new_code_sms")
    suspend fun getNewCode(@Query("phone") phone: String?, @Query("email") email: String?): Response<RegistrationResponse>

    @POST("user/new_code_sms")
    suspend fun getNewCodeGetError(@Query("phone") phone: String?, @Query("email") email: String?): Response<String>


    @POST("user/login")
    suspend fun login(@Body data: RegistrationData): Response<RegistrationResponse>

    @POST("user/login")
    suspend fun loginGetError(@Body data: RegistrationData): Response<String>

    @POST("upload/photo")
    suspend fun uploadPhoto(@Body photoData: PhotoData): Response<Unit>

    @POST("upload/photo")
    suspend fun uploadPhotoGetError(@Body photoData: PhotoData): Response<String>

}