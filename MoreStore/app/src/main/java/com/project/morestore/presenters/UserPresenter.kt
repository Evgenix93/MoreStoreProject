package com.project.morestore.presenters

import android.util.Log
import com.project.morestore.models.Size
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.repositories.UserRepository
import com.project.morestore.util.isEmailValid
import com.project.morestore.util.isPhoneValid
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class UserPresenter : MvpPresenter<UserMvpView>() {
    private val repository = UserRepository()

    fun changeUserData(
        phone: String? = null,
        email: String? = null,
        name: String? = null,
        surname: String? = null,
        sex: String? = null,
        step: Int? = null,
        code: Int? = null
    ) {
        presenterScope.launch {
            val unmaskedPhone =  phone?.filter { it != '(' && it != ')' && it != '-' }
            if(email != null && !email.isEmailValid()){
                viewState.error("почта указана неверно")
                return@launch
            }
            if(unmaskedPhone != null && !unmaskedPhone.isPhoneValid()){
                viewState.error("телефон указан неверно")
                return@launch
            }
            val response = repository.changeUserData(
                phone = unmaskedPhone,
                email = email,
                name = name,
                surname = surname,
                sex = sex,
                step = step,
                code = code
            )

            when(response?.code()){
                200 -> {
                    if(response.body()?.email?.err != null || response.body()?.phone?.err != null){
                        Log.d("mylog", response.body()?.email?.err.toString())
                        viewState.error("ошибка")
                        return@launch
                    }
                    viewState.success()
                }
                400 -> viewState.error("ошибка")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }

        }
    }





}