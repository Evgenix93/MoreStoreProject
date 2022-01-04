package com.project.morestore.presenters

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
            if(email != null){
                if(!email.isEmailValid()){
                    viewState.error("почта указана неверно")
                    return@launch
                }
            }
            if(phone != null){
                if(!phone.isPhoneValid()){
                    viewState.error("телефон указан неверно")
                    return@launch
                }
            }
            val response = repository.changeUserData(
                phone = phone,
                email = email,
                name = name,
                surname = surname,
                sex = sex,
                step = step,
                code = code
            )

            when(response?.code()){
                200 -> viewState.success()
                400 -> viewState.error("ошибка")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }

        }
    }
}