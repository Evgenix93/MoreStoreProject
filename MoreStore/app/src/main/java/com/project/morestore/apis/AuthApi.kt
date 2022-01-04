package com.project.morestore.apis

import com.project.morestore.models.RegistrationData
import com.project.morestore.models.RegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("user/registration")
    suspend fun emailRegister(@Body requestBody: RegistrationData): Response<RegistrationResponse>

}