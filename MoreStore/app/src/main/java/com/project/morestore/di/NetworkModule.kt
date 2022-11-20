package com.project.morestore.di

import com.project.morestore.apis.*
import com.project.morestore.data.models.CalendarAdapter

import com.project.morestore.util.TokenInterceptor
import com.squareup.moshi.Moshi

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun getClient(): OkHttpClient{
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
    fun getRetrofit(client: OkHttpClient): Retrofit{
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
    fun getAddressesNetwork(retrofit: Retrofit): AddressesNetwork{
        return retrofit.create(AddressesNetwork::class.java)
    }

    @Provides
    fun getAuthApi(retrofit: Retrofit): AuthApi{
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    fun getBrandApi(retrofit: Retrofit): BrandApi{
        return retrofit.create(BrandApi::class.java)
    }

    @Provides
    fun getCdekAddressApi(retrofit: Retrofit): CdekAddressApi{
        return retrofit.create(CdekAddressApi::class.java)
    }

    @Provides
    fun getCitiesApi(retrofit: Retrofit): CitiesApi{
        return retrofit.create(CitiesApi::class.java)
    }

    @Provides
    fun getCreditCardApi(retrofit: Retrofit): CreditCardsApi{
        return retrofit.create(CreditCardsApi::class.java)
    }

    @Provides
    fun getFilterApi(retrofit: Retrofit): FilterApi{
        return retrofit.create(FilterApi::class.java)
    }

    @Provides
    fun getGeoApi(retrofit: Retrofit): GeoApi{
        return retrofit.create(GeoApi::class.java)
    }

    @Provides
    fun getMessageApi(retrofit: Retrofit): MessageApi{
        return retrofit.create(MessageApi::class.java)
    }

    @Provides
    fun getOnBoardingApi(retrofit: Retrofit): OnboardingApi{
        return retrofit.create(OnboardingApi::class.java)
    }

    @Provides
    fun getOrdersApi(retrofit: Retrofit): OrdersApi{
        return retrofit.create(OrdersApi::class.java)
    }

    @Provides
    fun getProductApi(retrofit: Retrofit): ProductApi{
        return retrofit.create(ProductApi::class.java)
    }

    @Provides
    fun getReviewApi(retrofit: Retrofit): ReviewApi{
        return retrofit.create(ReviewApi::class.java)
    }

    @Provides
    fun getSalesApi(retrofit: Retrofit): SalesApi{
        return retrofit.create(SalesApi::class.java)
    }

    @Provides
    fun getUserApi(retrofit: Retrofit): UserApi{
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    fun getUserServerApi(retrofit: Retrofit): UserServerApi{
        return retrofit.create(UserServerApi::class.java)
    }



}