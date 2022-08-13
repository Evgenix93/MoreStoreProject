package com.project.morestore.apis

import com.project.morestore.models.Card
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CreditCardsApi {

    @GET("user/card")
    suspend fun getCards(): Response<List<Card>>

    @GET("user/card")
    suspend fun getCardsGetError(): Response<String>

    @POST("user/card")
    suspend fun addCard(@Body card: Card): Response<String>
}