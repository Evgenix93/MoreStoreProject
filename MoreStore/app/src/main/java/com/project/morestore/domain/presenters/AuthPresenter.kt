package com.project.morestore.domain.presenters

import android.content.Context
import com.project.morestore.util.isEmailValid


import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.project.morestore.R
import com.project.morestore.data.models.RegistrationData
import com.project.morestore.data.models.SocialType
import com.project.morestore.data.models.User
import com.project.morestore.presentation.mvpviews.AuthMvpView
import com.project.morestore.presentation.mvpviews.AuthPhoneMvpView
import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.util.errorMessage
import com.project.morestore.util.getStringFromResponse
import com.project.morestore.util.isPhoneValid
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthPresenter @Inject constructor(@ActivityContext private val context: Context,
                                        private val repository: AuthRepository) : MvpPresenter<AuthMvpView>() {

    fun register(
        phone: String? = null,
        email: String? = null,
        step: Int,
        type: Int,
        user: Int? = null,
        code: String? = null,
        name: String? = null,
        surname: String? = null
    ) {
        presenterScope.launch {
            if (phone != null && !phone.trim().isPhoneValid()) {
                viewState.error(context.getString(R.string.phone_format_incorrect))
                return@launch
            }
            if (email != null && !email.trim().isEmailValid()) {
                viewState.error(context.getString(R.string.email_format_incorrect))
                return@launch
            }
            viewState.loading()

            val response = repository.register(
                RegistrationData(
                    step = step,
                    phone = if(phone?.contains("+7") == true) phone.trim() else phone?.trim()?.replaceFirstChar {'7'},
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
                    if (step == 2) {
                        repository.setupToken(response.body()!!.token!!)
                        repository.saveToken(response.body()!!.token!!, response.body()!!.expires!!)
                        getUserData()
                    }
                    if (step == 1) {
                        viewState.success(response.body()!!)
                    }
                }
                400 -> {
                    val bodyString = response.errorBody()!!.getStringFromResponse()
                    if (step == 1) {
                        if (bodyString.contains("Этот номер зарегистрирован") || bodyString
                                .contains("Эта почта зарегистрирована")
                        ) {
                            login(
                                phone = phone,
                                email = email,
                                step = step,
                                type = type,
                                user = user,
                                code = code,
                                isFromRegistration = true
                            )
                        } else {
                            viewState.error(bodyString)
                        }
                    } else {
                        viewState.error(bodyString)
                    }
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun login(
        phone: String? = null,
        email: String? = null,
        step: Int,
        type: Int,
        user: Int? = null,
        code: String? = null,
        isFromRegistration: Boolean = false
    ) {
        presenterScope.launch {
            if (phone != null && !phone.trim().isPhoneValid()) {
                viewState.error(context.getString(R.string.phone_format_incorrect))
                return@launch
            }
            if (email != null && !email.trim().isEmailValid()) {
                viewState.error(context.getString(R.string.email_format_incorrect))
                return@launch
            }
            viewState.loading()
            val response = repository.login(
                RegistrationData(
                    phone = if(phone?.contains("+7") == true) phone.trim() else phone?.trim()?.replaceFirstChar {'7'},
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
                        repository.saveToken(response.body()?.token!!, response.body()!!.expires!!)
                        getUserData()
                    } else {
                        viewState.success(response.body()!!, if(isFromRegistration) true else null)
                    }
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getUserData() {
        presenterScope.launch {
            viewState.loading()
            val firebaseToken = getFcmToken()
            firebaseToken ?: return@launch
            val response = repository.getUserData(firebaseToken)
            when (response?.code()) {
                200 -> {
                    val user = response.body()!!
                    repository.setupUserId(user.id)
                    if (checkUserData(user)) {
                        viewState.registrationComplete(true, user)
                    } else {
                        viewState.registrationComplete(false, user)
                    }

                }
               else -> viewState.error(errorMessage(response))
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
                    (viewState as AuthPhoneMvpView).successNewCode(response.body()!!)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getSocialLoginUrl(type: String) {
        viewState.loading()
        presenterScope.launch {
            val response = repository.getSocialLoginUrl(SocialType(type))
            when (response?.code()) {
                200 -> viewState.success(response.body()!!)
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun loginSocial(url: String) {
        viewState.loading()
        presenterScope.launch {
            val response = repository.loginSocial(url)
            when (response?.code()) {
                200 -> {
                    repository.setupToken(response.body()?.token!!)
                    repository.saveToken(response.body()?.token.orEmpty(), response.body()?.expires ?: 0)
                    viewState.success(response.body()!!)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    private fun checkUserData(user: User): Boolean {
        return user.name != null && user.surname != null && user.phone != null

    }

    private suspend fun getFcmToken() : String? {
        return suspendCoroutine {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d("mylog", "token $token")
                    it.resume(token)

                }
                .addOnFailureListener { exception ->
                    Log.d("mylog", exception.message.toString())
                    viewState.error(exception.message.orEmpty())
                    it.resume(null)
                }
        }
    }
}