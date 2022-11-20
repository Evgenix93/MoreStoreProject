package com.project.morestore.apis

import com.project.morestore.data.models.Card
import com.project.morestore.data.models.Id
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

    @POST("user/delete_card")
    suspend fun deleteCard(@Body cardId: Id): Response<Unit>
}