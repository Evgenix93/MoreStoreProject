package com.project.morestore.repositories

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.project.morestore.models.*
import com.project.morestore.singletones.Network
import com.project.morestore.singletones.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.File
import java.io.IOException

class AuthRepository(private val context: Context) {

    private val authApi = Network.authApi


    suspend fun register(data: RegistrationData): Response<RegistrationResponse>? {
        return try {
            clearToken()
            authApi.register(data)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = authApi.registerGetError(data)
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun login(data: RegistrationData): Response<RegistrationResponse>? {
        return try {
            clearToken()
            authApi.login(data)
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = authApi.loginGetError(data)
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }


    }

    suspend fun getUserData(): Response<User>?{
        Log.d("mylog", "getUserData")
        return try {
            authApi.getUserData()
        }catch (e: Throwable){
            if(e is IOException){
                null
            }else{
                Log.d("mylog", e.message.toString())
                Response.error(400, "ошибка".toResponseBody(null))
                //try {
                   // val response = authApi.loginGetError(data)
                   // if(response.code() == 500){
                      //  Response.error(500, "".toResponseBody(null))
                   // }else {
                     //   Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                   // }
                //}catch (e: Throwable){
                 //   Response.error(400, "ошибка".toResponseBody(null))
               // }

            }
        }
    }


    suspend fun getNewCode(
        phone: String? = null,
        email: String? = null
    ): Response<RegistrationResponse>? {
        return try {
            clearToken()
            authApi.getNewCode(phone, email)
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = authApi.getNewCodeGetError(phone, email)
                    if(response.code() == 500){
                        Response.error(500, "".toResponseBody(null))
                    }else {
                        Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                    }
                }catch (e: Throwable){
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getSocialLoginUrl(type: SocialType): Response<String>?{
        return try {
            clearToken()
            authApi.getSocialLoginUrl(type)
        }catch (e: Throwable){
            if(e is IOException){
                null
            }else{
                Response.error(400, "ошибка".toResponseBody(null))
            }
        }
    }

    suspend fun loginSocial(url: String): Response<RegistrationResponse>?{
        return try {
            clearToken()
            authApi.loginSocial(url)
        }catch (e: Throwable){
            if(e is IOException){
                null
            }else{
                Response.error(400, "ошибка".toResponseBody(null))
            }
        }

    }

    fun setupToken(token: String) {
        Token.token = "Bearer $token"
    }





    fun isTokenEmpty(): Boolean{
        Log.d("Debug", "token isEmpty = ${Token.token.isEmpty()}")
        return Token.token.isEmpty()
    }

    fun clearToken(){
        Token.token = ""
    }

}