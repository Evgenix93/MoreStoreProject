package com.project.morestore.apis

import com.project.morestore.models.RegistrationData
import com.project.morestore.models.RegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query



interface AuthApi {

    @POST("user/registration")
    suspend fun phoneRegister1(@Body data: RegistrationData): Response<RegistrationResponse?>?


    @POST("user/new_code_sms")
    suspend fun getNewCode(@Query("phone") phone: String?, @Query("email") email: String?): Response<RegistrationResponse>


    @POST("user/registration")
    suspend fun emailRegister(@Body requestBody: RegistrationData): Response<RegistrationResponse>

    @POST("user/login")
    suspend fun login1(@Body data: RegistrationData): Response<RegistrationResponse>

}