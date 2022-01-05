package com.project.morestore.repositories

import android.util.Log
import com.project.morestore.models.RegistrationData
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.singletones.Network
import com.project.morestore.singletones.Token
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class AuthRepository {

    private val authApi = Network.authApi


    suspend fun register(data: RegistrationData): Response<RegistrationResponse?>?{
        return try {
            authApi.phoneRegister1(data)
        }catch (e: Throwable){
            Log.d("mylog", e.message.toString() )
            if(e is IOException) {
                null
            }else{
                Response.error(400, "".toResponseBody(null))
            }
        }
    }

    //suspend fun login(data: RegistrationData): Response<RegistrationResponse>?{



    //}


    suspend fun getNewCode(phone: String? = null, email: String? = null): Response<RegistrationResponse>? {
        return try {
            authApi.getNewCode(phone, email)
        }catch (e: Throwable){
            if(e is IOException) {
                null
            }else{
                Response.error(400, "".toResponseBody(null))
            }
        }
    }

    fun setupToken(token: String){
        Token.token = "Bearer $token"
    }



}