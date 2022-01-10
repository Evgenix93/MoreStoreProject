package com.project.morestore.repositories

import android.content.Context
import android.content.SharedPreferences
import com.project.morestore.models.*
import com.project.morestore.singletones.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException

class UserRepository() {
    private val userApi = Network.userApi

    suspend fun changeUserData(
        phone: String? = null,
        email: String? = null,
        name: String? = null,
        surname: String? = null,
        sex: String? = null,
        step: Int? = null,
        code: Int? = null
    ): Response<RegistrationResponse>? {
        return try {
            userApi.changeUserData(
                email = email,
                phone = phone,
                name = name,
                surname = surname,
                sex = sex,
                step = step,
                code = code
            )
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.changeUserDataGetError(
                        email = email,
                        phone = phone,
                        step = step,
                        code = code,
                        name = name,
                        surname = surname,
                        sex = sex
                    )
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }

    }

    suspend fun changeUserData2(
        phone: String? = null,
        email: String? = null,
        step: Int? = null,
        code: Int? = null
    ): Response<RegistrationResponse2>? {
        return try {
            userApi.changeUserData2(
                email = email,
                phone = phone,
                step = step,
                code = code
            )
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.changeUserData2GetError(
                        email = email,
                        phone = phone,
                        step = step,
                        code = code
                    )
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }

            }
        }

    }


}