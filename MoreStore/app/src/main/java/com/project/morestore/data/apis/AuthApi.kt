package com.project.morestore.data.apis

import com.project.morestore.data.models.*
import retrofit2.Response
import retrofit2.http.*


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



    @GET("user/islogin")
    suspend fun getUserData(@Query("firebase_token") token: String?): Response<User>

    @GET("user/islogin")
    suspend fun getUserDataGetError(@Query("firebase_token") token: String?): Response<String>


    @POST("user/login/social/get_url")
    suspend fun getSocialLoginUrl(@Body type: SocialType): Response<String>

    @GET()
    suspend fun loginSocial(@Url url: String): Response<RegistrationResponse>

}