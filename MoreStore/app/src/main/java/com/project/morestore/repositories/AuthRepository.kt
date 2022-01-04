package com.project.morestore.repositories

import android.util.Log
import com.project.morestore.models.RegistrationData
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.singletones.Network
<<<<<<< HEAD
import com.project.morestore.singletones.Token
=======
>>>>>>> origin/main
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class AuthRepository {
<<<<<<< HEAD
    private val authApi = Network.authApi


    suspend fun phoneRegister(data: RegistrationData): Response<RegistrationResponse?>?{
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

=======
  suspend fun emailRegister1(email: String): Response<RegistrationResponse>?{
    return try {
         val registrationData = RegistrationData(
             1,
             null,
             null,
             email,
             null,
             null,
             2
         )
         Network.authApi.emailRegister(registrationData)
     }catch(e: Exception){
       Log.e("Debug", "error = ${e.message}")
       if(e is IOException)
           null
       else
          Response.error(400, "".toResponseBody())
     }
   }

  suspend fun emailRegister2(user:Int, code: Int): Response<RegistrationResponse>?{
      return try{
          val registrationData = RegistrationData(
              2,
              user,
              code,
              null,
              null,
              null,
              2
          )
          Network.authApi.emailRegister(registrationData)
      }catch (e: Exception){
          if(e is IOException)
              null
          else
              Response.error(400, "".toResponseBody())
      }
  }

    suspend fun emailRegister3(user: Int, code: Int, name: String, surname: String): Response<RegistrationResponse>?{
      return try {  val registrationData = RegistrationData(
            3,
            user,
            code,
            null,
            name,
            surname,
            2
        )
        Network.authApi.emailRegister(registrationData)
    }catch (e: Exception){
          if(e is IOException)
              null
          else
              Response.error(400, "".toResponseBody())
      }
    }
>>>>>>> origin/main
}