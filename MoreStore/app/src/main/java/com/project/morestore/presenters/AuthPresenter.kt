package com.project.morestore.presenters

import android.content.Context
import android.net.Uri
import com.project.morestore.util.isEmailValid


import android.util.Log
import com.project.morestore.models.RegistrationData
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.util.isPhoneValid
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class AuthPresenter(context: Context) : MvpPresenter<AuthMvpView>() {

    private val repository = AuthRepository(context)
    private var photoUri: Uri? = null


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
            if (phone != null && !phone.isPhoneValid()) {
                viewState.error("телефон указан неверно")
                return@launch
            }
            if(email != null && !email.isEmailValid()){
                viewState.error("почта указана неверно")
                return@launch
            }
            viewState.loading()
            val response = repository.register(
                RegistrationData(
                    step = step,
                    phone = phone,
                    email = email,
                    type = type,
                    user = user,
                    code = code,
                    name = name,
                    surname = surname
                )
            )
            when (response?.code()) {
                200 -> {
                    if (step == 3)
                        repository.setupToken(response.body()!!.token!!)
                    if (photoUri != null) {
                        Log.d("Debug", "photoUri = $photoUri")
                        uploadPhoto(photoUri!!)
                    }
                    else {
                        Log.d("Debug", "photoUri = null")
                        viewState.success(response.body()!!)
                    }
                }
                400 -> if(step == 1) getNewCode(phone, email)
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
    ){
        presenterScope.launch {
            if (phone != null && !phone.isPhoneValid()) {
                viewState.error("телефон указан неверно")
                return@launch
            }
            if(email != null && !email.isEmailValid()){
                viewState.error("почта указана неверно")
                return@launch
            }
            viewState.loading()
            val response = repository.login(
                RegistrationData(
                phone = phone,
                    email = email,
                    step = step,
                    type = type,
                    user = user,
                    code = code,
                    name = null,
                    surname = null
            ))

            when(response?.code()){
                200 -> {
                    if(step == 2){
                        repository.setupToken(response.body()?.token!!)
                    }
                    viewState.success(response.body()!!)
                }
                400 -> viewState.error("ошибка")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }
        }


    }





    fun getNewCode(phone: String? = null, email: String? = null) {
        presenterScope.launch {
            Log.d("mylog", "getNewCode")
            viewState.loading()
            val response = repository.getNewCode(phone, email)
            when (response?.code()) {
                200 -> {
                    viewState.success(response.body()!!)
                }
                400 -> viewState.error("ошибка")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }
        }
    }

    fun safePhotoUri(uri: Uri){
        photoUri = uri
    }


  private fun uploadPhoto(uri: Uri){
     presenterScope.launch {
         val response = repository.uploadPhoto(uri)
         when (response?.code()) {
             200 -> {viewState.success(Unit)}
             400 -> {viewState.error("ошибка")}
             null -> viewState.error("нет интернета")
             else -> viewState.error("ошибка")
         }
     }
   }






}