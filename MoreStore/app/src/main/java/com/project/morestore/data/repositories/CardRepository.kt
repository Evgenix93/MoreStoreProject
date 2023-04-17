package com.project.morestore.data.repositories

import android.util.Log
import com.project.morestore.data.apis.CreditCardsApi
import com.project.morestore.data.models.Card
import com.project.morestore.data.models.CardActiveData
import com.project.morestore.data.models.Id
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class CardRepository @Inject constructor(private val cardApi: CreditCardsApi) {


    suspend fun getCards(): Response<List<Card>>? {
        return try {
            cardApi.getCards()
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = cardApi.getCardsGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: e.message.toString().toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, e.message.toString().toResponseBody(null))
                }
            }
        }
    }

    suspend fun addCard(card: Card): Response<String>? {
        return try {
            cardApi.addCard(card)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                Response.error(400, e.message.toString().toResponseBody(null))
            }
        }
    }

    suspend fun deleteCard(card: Card): Response<Unit>? {
        return try {
            cardApi.deleteCard(Id(card.id!!))
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                Response.error(400, e.message.toString().toResponseBody(null))
            }
        }
    }

    suspend fun chooseCard(cardId: Long, activeData: CardActiveData): Response<String>? {
        return try {
            cardApi.chooseCard(cardId, activeData)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                Response.error(400, e.message.toString().toResponseBody(null))
            }
        }
    }
}