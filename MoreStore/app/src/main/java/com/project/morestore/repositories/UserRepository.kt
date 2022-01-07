package com.project.morestore.repositories

import android.content.Context
import android.content.SharedPreferences
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.models.Size
import com.project.morestore.models.SizeJsonAdapter
import com.project.morestore.singletones.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
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
            if(e is IOException) {
                null
            }else{
                Response.error(400, "".toResponseBody(null))
            }
        }

    }



}