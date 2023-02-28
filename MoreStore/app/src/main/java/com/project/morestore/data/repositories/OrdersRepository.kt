package com.project.morestore.data.repositories

import android.util.Log
import com.project.morestore.data.apis.OrdersApi
import com.project.morestore.data.models.*
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class OrdersRepository @Inject constructor(private val ordersApi: OrdersApi) {





    suspend fun orderProblem(productProblem: ProductProblemsData) {
        try {
            ordersApi.sendProblem(productProblem)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun createOrder(order: NewOrder): Response<Unit>?{
        return try {
            ordersApi.createOrder(order)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.createOrderGetError(order)
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

    suspend fun getAllOrders(): Response<List<Order>>?{
        return try {
            ordersApi.getAllOrders()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.getAllOrdersGetError()
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

    suspend fun getOrderAddresses(): Response<List<OfferedOrderPlace>>?{
        return try {
            ordersApi.getOrderAddresses()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.getOrderAddressesGetError()
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

    suspend fun changeOrderPlaceStatus(change: OfferedOrderPlaceChange): Response<Unit>?{
        return try {
            ordersApi.changeOrderPlaceStatus(change)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.changeOrderPlaceStatusGetError(change)
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

    suspend fun submitReceiveOrder(orderId: OrderId): Response<Unit>?{
        return try {
            ordersApi.submitReceiveOrder(orderId)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.submitReceiveOrderGetError(orderId)
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

    suspend fun getPromoInfo(code: String): Response<PromoCode>?{
        return try {
            ordersApi.getPromoCodeInfo(code)
        }catch (e: Exception){
            if (e is IOException){
                null
            }else{
                Response.error(400, e.message.toString().toResponseBody(null))
            }
        }
    }

    suspend fun payForOrder(payInfo: PayOrderInfo): Response<PaymentUrl>?{
        return try {
            ordersApi.payOrder(payInfo)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.payOrderGetError(payInfo)
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

   suspend fun returnMoney(returnMoneyInfo: ReturnMoneyInfo): Response<Unit>?{
       return try {
           ordersApi.returnMoney(returnMoneyInfo)
       }catch(e: Exception){
               null
       }
   }

    suspend fun getCdekPrice(info: CdekCalculatePriceInfo): Response<DeliveryPrice>?{
        return try {
            ordersApi.getCdekPrice(info)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.getCdekPriceError(info)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.errors?.first()?.message?.toResponseBody(null) ?: e.message.toString().toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, e.message.toString().toResponseBody(null))
                }
            }
        }
    }

    suspend fun createCdekOrder(order: CdekOrder): Response<CreateCdekResponse>? {
        return try {
            ordersApi.createCdekOrder(order)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.createCdekOrderGetError(order)
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

    suspend fun createYandexGoOrder(order: YandexGoOrder): Response<YandexOrderInfoBody>? {
        return try {
            ordersApi.createYandexGoOrder(order)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.createYandexGoOrderGetError(order)
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

    suspend fun getYandexGoOrderInfo(id: String): Response<YandexOrderInfoBody>? {
        return try {
            ordersApi.getYandexGoOrderInfo(id)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", "get yandex info: ${e.message.toString()}")
                try {
                    val response = ordersApi.getYandexGoOrderInfoGetError(id)
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

    suspend fun submitYandexGoOrder(claimId: YandexClaimId): Response<YandexSubmitResult>? {
        return try {
            ordersApi.submitYandexGoOrder(claimId)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.submitYandexGoOrderGetError(claimId)
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

    suspend fun getCdekOrderInfo(id: String): Response<CdekOrderInfo>? {
        return try {
            ordersApi.getCdekOrderInfo(id)
        }catch (e: Throwable){
            Log.d("mylog", "get Cdek info: ${e.message.toString()}")
            null
        }
    }

    suspend fun getYandexGoPrice(info: YandexPriceCalculateInfo): Response<YandexPriceResult>? {
        return try {
            ordersApi.getYandexGoPrice(info)
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.getYandexGoPriceGetError(info)
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

    suspend fun cancelYandexGoOrder(claimId: YandexCancelClaimId): Response<YandexCancelResult>? {
        return try {
            ordersApi.cancelYandexGoOrder(claimId)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.cancelYandexGoOrderGetError(claimId)
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



}