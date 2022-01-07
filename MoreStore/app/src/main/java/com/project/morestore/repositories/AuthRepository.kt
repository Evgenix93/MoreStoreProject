package com.project.morestore.repositories

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.project.morestore.models.Photo
import com.project.morestore.models.PhotoData
import com.project.morestore.models.RegistrationData
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.singletones.Network
import com.project.morestore.singletones.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.File
import java.io.IOException

class AuthRepository(private val context: Context) {

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

   suspend fun uploadPhoto(uri: Uri): Response<Unit>? {
      return withContext(Dispatchers.IO) {
           val file = File(context.cacheDir, "photo.jpeg")
           context.contentResolver.openInputStream(uri).use { input ->
               file.outputStream().use { output ->
                   input?.copyTo(output)
               }

           }
           val photo = Photo("jpeg", Base64.encodeToString(file.readBytes(), Base64.DEFAULT))
           val photoData = PhotoData(
               "EditUser",
               7,
               listOf(photo)
           )
           try {
           Network.authApi.uploadPhoto(photoData)
       }catch (e: Exception){
               if(e is IOException) {
                   null
               }else{
                   Response.error(400, "".toResponseBody(null))
               }
       }
       }
   }

}