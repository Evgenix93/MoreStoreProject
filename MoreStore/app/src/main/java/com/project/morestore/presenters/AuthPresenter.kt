package com.project.morestore.presenters

import com.project.morestore.util.isEmailValid


import android.util.Log
import com.project.morestore.models.RegistrationData
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.util.isPhoneValid
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class AuthPresenter : MvpPresenter<AuthMvpView>() {

    private val repository = AuthRepository()


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
                200 -> viewState.success(response.body()!!)
                400 -> if(step == 1) getNewCode(phone, email)
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











}