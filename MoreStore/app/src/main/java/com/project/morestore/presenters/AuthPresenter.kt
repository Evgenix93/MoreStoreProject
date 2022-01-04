package com.project.morestore.presenters

import android.util.Log
import com.project.morestore.models.RegistrationData
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.util.isPhoneValid
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class AuthPresenter: MvpPresenter<AuthMvpView>() {
    private val repository = AuthRepository()


    fun phoneRegister1(phone: String){
        presenterScope.launch {
            if(!phone.isPhoneValid()){
                viewState.error("телефон указан неверно")
                return@launch
            }
            viewState.loading()
            val response = repository.phoneRegister(RegistrationData(
                step = 1,
                phone = phone,
                type = 1,
                user = null,
                code = null,
                name = null,
                surname = null
            ))
            when(response?.code()){
                200 -> viewState.success(response.body()!!)
                400 -> getNewCode(phone)
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }

        }



    }

    fun phoneRegister2(code: Int, userId: Int){
        presenterScope.launch {
            viewState.loading()
            val response = repository.phoneRegister(RegistrationData(
                step = 2,
                user = userId,
                code = code,
                type = 1,
                phone = null,
                name = null,
                surname = null
            ))
            when(response?.code()){
                200 -> viewState.success(response.body()!!)
                400 -> viewState.error("ошибка")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }
        }
    }

    fun phoneRegister3(code: Int, userId: Int, name: String, surname: String){
        presenterScope.launch {
            viewState.loading()
            val response = repository.phoneRegister(RegistrationData(
                step = 3,
                user = userId,
                code = code,
                type = 1,
                phone = null,
                name = name,
                surname = surname
            ))
            when(response?.code()){
                200 -> {
                    repository.setupToken(response.body()?.token!!)
                    viewState.success(response.body()!!)
                }
                400 -> viewState.error("ошибка")
                null -> viewState.error("нет игтернета")
                else -> viewState.error("ошибка")
            }
        }

    }

    fun getNewCode(phone: String? = null, email: String? = null){
        presenterScope.launch {
            Log.d("mylog", "getNewCode")
            viewState.loading()
            val response = repository.getNewCode(phone, email)
            when (response?.code()) {
                200 -> {}
                400 -> viewState.error("ошибка")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }
        }
    }


}