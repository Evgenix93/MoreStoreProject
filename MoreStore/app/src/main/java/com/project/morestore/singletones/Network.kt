package com.project.morestore.singletones

import com.project.morestore.apis.*
import com.project.morestore.models.CalendarAdapter

import com.project.morestore.util.TokenInterceptor
import com.squareup.moshi.Moshi
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
        .callTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://morestore.app-rest.ru/api/v1/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(
            Moshi.Builder().add(CalendarAdapter).build()
        ))
        .build()

    val authApi: AuthApi
    get() = retrofit.create()

    val userApi: UserApi
    get() = retrofit.create()

    val onBoardingApi: OnboardingApi
    get() = retrofit.create()

    val productApi: ProductApi
    get() = retrofit.create()

    val filterApi: FilterApi
    get() = retrofit.create()

    val chatApi: MessageApi
    get() = retrofit.create()

    val reviewApi :ReviewApi
    get() = retrofit.create()

    val cities :CitiesApi get() = retrofit.create()

    val geo :GeoApi get() = retrofit.create()
    val userServerApi :UserServerApi get() = retrofit.create()
    val addresses :AddressesNetwork get() = retrofit.create()
    val cdekAddresses :CdekAddressApi get() = retrofit.create()

    val ordersApi :OrdersApi
    get() = retrofit.create()
    val salesApi: SalesApi get() = retrofit.create()

    val brandApi :BrandApi get() = retrofit.create()
}