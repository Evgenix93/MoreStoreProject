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

    suspend fun getUserData(fireBaseToken: String? = null): Response<User>?{
        Log.d("mylog", "getUserData")
        return try {
            authApi.getUserData(fireBaseToken)
        }catch (e: Throwable){
            if(e is IOException){
                null
            }else{
                Log.d("mylog", e.message.toString())
                try {
                    val response = authApi.getUserDataGetError(fireBaseToken)
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

    suspend fun saveToken(token: String, expires: Long){
        try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(TOKEN_PREF, Context.MODE_PRIVATE)
                prefs.edit().apply(){
                    putString(TOKEN_KEY, token)
                    putString(TOKEN_GET_TIME_SAVE, System.currentTimeMillis().toString())
                    putString(TOKEN_EXPIRES, expires.toString())
                }.commit()


            }
        }catch (e: Throwable){
            Log.d("error", e.message.toString())
        }
    }

    suspend fun loadToken(): String? {
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(TOKEN_PREF, Context.MODE_PRIVATE)
                prefs.getString(TOKEN_KEY, null)
            }
        }catch (e: Throwable){
            Log.d("error", e.message.toString())
            null
        }
    }

    suspend fun loadTokenSaveTime(): Long?{
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(TOKEN_PREF, Context.MODE_PRIVATE)
                prefs.getString(TOKEN_GET_TIME_SAVE, null)?.toLong()
            }
        }catch (e: Throwable){
            Log.d("error", e.message.toString())
            null
        }
    }

    suspend fun loadTokenExpires(): Long?{
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(TOKEN_PREF, Context.MODE_PRIVATE)
                prefs.getString(TOKEN_EXPIRES, null)?.toLong()
            }
        }catch (e: Throwable){
            Log.d("error", e.message.toString())
            null
        }
    }



    fun setupUserId(userId: Long){
        Token.userId = userId
    }

    fun getUserId(): Long{
        return Token.userId
    }

    fun getToken(): String = Token.token



    fun isTokenEmpty(): Boolean{
        Log.d("Debug", "token isEmpty = ${Token.token.isEmpty()}")
        return Token.token.isEmpty()
    }

    fun clearToken(){
        Token.token = ""
        Token.userId = 0
    }

    companion object{
        const val TOKEN_PREF = "token_pref"
        const val TOKEN_KEY = "token_key"
        const val TOKEN_GET_TIME_SAVE = "token_save_time_key"
        const val TOKEN_EXPIRES = "token_expires"
    }

}