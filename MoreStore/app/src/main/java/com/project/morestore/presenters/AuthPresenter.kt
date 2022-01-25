package com.project.morestore.presenters

import android.content.Context
import android.net.Uri
import com.project.morestore.util.isEmailValid


import android.util.Log
import com.project.morestore.models.RegistrationData
import com.project.morestore.models.SocialType
import com.project.morestore.models.User
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import com.project.morestore.util.isPhoneValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class AuthPresenter(context: Context) : MvpPresenter<AuthMvpView>() {

    private val repository = AuthRepository(context)
    



    fun register(
        phone: String? = null,
        email: String? = null,
        step: Int,
        type: Int,
        user: Int? = null,
        code: Int? = null,
        name: String? = null,
        surname: String? = null
    ) {
        presenterScope.launch {
            if (phone != null && !phone.trim().isPhoneValid()) {
                viewState.error("телефон указан неверно")
                return@launch
            }
            if (email != null && !email.trim().isEmailValid()) {
                viewState.error("почта указана неверно")
                return@launch
            }
            viewState.loading()

            val response = repository.register(
                RegistrationData(
                    step = step,
                    phone = phone?.trim(),
                    email = email?.trim(),
                    type = type,
                    user = user,
                    code = code,
                    name = name,
                    surname = surname
                )
            )
            when (response?.code()) {
                200 -> {
                    if(step == 2) {
                        repository.setupToken(response.body()!!.token!!)
                        getUserData()
                    }
                    if(step == 1){
                        viewState.success(response.body()!!)
                    }
                    //if(step == 3) {
                       // if (photoUri != null) {
                         //   Log.d("Debug", "photoUri = $photoUri")
                        //    uploadPhoto(photoUri!!)
                      //  }else{
                        //    viewState.success(response.body()!!)
                      //  }
                    //}//else {
                       // Log.d("Debug", "photoUri = null")
                      //  viewState.success(response.body()!!)
                   // }


                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    if (step == 1) {
                        if (bodyString.contains("Этот номер зарегистрирован") || bodyString
                                .contains("Эта почта зарегистрирована")
                        ) {
                            getNewCode(phone, email)
                            //viewState.error("401")
                        } else {
                            viewState.error(bodyString)
                        }
                    }else{
                        viewState.error(bodyString)
                    }
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }

        }


    }

    fun login(
        phone: String? = null,
        email: String? = null,
        step: Int,
        type: Int,
        user: Int? = null,
        code: Int? = null
    ) {
        presenterScope.launch {
            if (phone != null && !phone.trim().isPhoneValid()) {
                viewState.error("телефон указан неверно")
                return@launch
            }
            if (email != null && !email.trim().isEmailValid()) {
                viewState.error("почта указана неверно")
                return@launch
            }
            viewState.loading()
            val response = repository.login(
                RegistrationData(
                    phone = phone?.trim(),
                    email = email?.trim(),
                    step = step,
                    type = type,
                    user = user,
                    code = code,
                    name = null,
                    surname = null
                )
            )

            when (response?.code()) {
                200 -> {
                    if (step == 2) {
                        repository.setupToken(response.body()?.token!!)
                        getUserData()
                    }else {
                        viewState.success(response.body()!!)
                    }
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }
        }


    }

    fun getUserData(){
        presenterScope.launch {
            viewState.loading()
            val response = repository.getUserData()
            when(response?.code()){
                200 -> {
                    val user = response.body()!!
                    if(checkUserData(user)){
                        viewState.registrationComplete(true, user)
                    }else{
                        viewState.registrationComplete(false, user)
                    }

                }
                400 -> {viewState.error("Ошибка")}
                null -> viewState.error("Нет интернета")
            }
        }
    }


    fun getNewCode(phone: String? = null, email: String? = null) {
        presenterScope.launch {
            Log.d("mylog", "getNewCode")
            viewState.loading()
            val response = repository.getNewCode(phone?.trim(), email?.trim())
            when (response?.code()) {
                200 -> {
                    viewState.successNewCode(response.body()!!)
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }
        }
    }

    fun getSocialLoginUrl(type: String){
        viewState.loading()
        presenterScope.launch {
            val response = repository.getSocialLoginUrl(SocialType(type))
            when(response?.code()){
                200 -> viewState.success(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("Нет интернета")
            }
        }
    }

    fun loginSocial(url: String){
        viewState.loading()
        presenterScope.launch {
            val response = repository.loginSocial(url)
            when(response?.code()){
                200 -> {
                    repository.setupToken(response.body()?.token!!)
                    viewState.success(response.body()!!)
                }
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("Нет интернета")
            }
        }
    }







    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }


    }

    private fun checkUserData(user: User): Boolean{
        return user.name != null && user.surname != null && user.phone != null

    }


}