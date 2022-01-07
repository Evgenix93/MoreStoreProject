package com.project.morestore.singletones

import com.project.morestore.apis.AuthApi
import com.project.morestore.apis.OnBoardingApi
import com.project.morestore.apis.UserApi
import com.project.morestore.util.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object Network {
    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addNetworkInterceptor(TokenInterceptor())
        .callTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://morestore.app-rest.ru/api/v1/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val authApi: AuthApi
    get() = retrofit.create()

    val userApi: UserApi
    get() = retrofit.create()

    val onBoardingApi: OnBoardingApi
    get() = retrofit.create()
}