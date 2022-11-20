package com.project.morestore

import android.content.Context
import com.project.morestore.apis.BrandApi
import com.project.morestore.models.CalendarAdapter
import com.project.morestore.singletones.Network
import com.project.morestore.util.TokenInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


@Module
@InstallIn()
class NetworkModule {

    @Provides
    fun provideClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(TokenInterceptor())
            .callTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://vm-f0c61e3b.na4u.ru/api/v1/")
            .client(client)
            .addConverterFactory(
                MoshiConverterFactory.create(
                Moshi.Builder().add(CalendarAdapter).build()
            ))
            .build()
    }

    @Provides
    fun provideBrandApi(retrofit: Retrofit): BrandApi{
       return retrofit.create(BrandApi::class.java)
    }
}