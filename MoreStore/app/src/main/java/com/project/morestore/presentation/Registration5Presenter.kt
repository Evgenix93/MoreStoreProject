package com.project.morestore.presentation

import android.content.Context
import com.project.morestore.R
import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.presentation.mvpviews.Registration5View
import com.project.morestore.presentation.mvpviews.RegistrationMvpView
import com.project.morestore.util.errorMessage
import com.project.morestore.util.isEmailValid
import com.project.morestore.util.isPhoneValid
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class Registration5Presenter(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
): MvpPresenter<Registration5View>() {

    fun changeUserData2(
        phone: String? = null,
        email: String? = null,
        step: Int? = null,
        code: String? = null
    ) {
        presenterScope.launch {
            viewState.loading()
            val unmaskedPhone = phone?.filter { it != '(' && it != ')' && it != '-' }
            if (email != null && !email.trim().isEmailValid()) {
                viewState.error(context.getString(R.string.email_format_incorrect))
                return@launch
            }
            if (unmaskedPhone != null && !unmaskedPhone.trim().isPhoneValid()) {
                viewState.error(context.getString(R.string.phone_format_incorrect))
                return@launch
            }
            val response = userRepository.changeUserData2(
                phone = unmaskedPhone?.trim(),
                email = email?.trim(),
                step = step,
                code = code
            )

            when (response?.code()) {
                200 -> {
                    viewState.success()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getNewCode(phone: String? = null, email: String? = null) {
        presenterScope.launch {
            viewState.loading()
            val response = authRepository.getNewCode(phone?.trim(), email?.trim())
            when (response?.code()) {
                200 -> {
                    viewState.successNewCode()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }
}