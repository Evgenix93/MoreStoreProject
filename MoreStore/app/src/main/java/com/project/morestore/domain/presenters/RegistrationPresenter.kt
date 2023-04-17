package com.project.morestore.domain.presenters

import android.content.Context
import android.net.Uri
import android.util.Log
import com.project.morestore.R
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.presentation.mvpviews.RegistrationMvpView
import com.project.morestore.util.errorMessage
import com.project.morestore.util.isEmailValid
import com.project.morestore.util.isPhoneValid
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class RegistrationPresenter @Inject constructor(@ActivityContext private val context: Context,
                                                private val userRepository: UserRepository
): MvpPresenter<RegistrationMvpView>() {
    private var photoUri: Uri? = null



    fun safePhotoUri(uri: Uri) {
        photoUri = uri
    }

    private fun uploadPhoto(uri: Uri) {
        presenterScope.launch {
            val response = userRepository.uploadPhoto(uri)
            when (response?.code()) {
                200 -> {
                    viewState.success()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

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
            if (step == null && phone == null && email == null && name.isNullOrBlank()) {
                viewState.error(context.getString(R.string.write_your_name))
                return@launch
            }
            if (step == null && phone == null && email == null && surname.isNullOrBlank()) {
                viewState.error(context.getString(R.string.write_your_second_name))
                return@launch
            }
            val response = userRepository.changeUserData(
                phone = unmaskedPhone?.trim(),
                email = email?.trim(),
                name = name,
                surname = surname,
                sex = sex,
                step = step,
                code = code
            )

            when (response?.code()) {
                200 -> {
                    if (photoUri != null) {
                        Log.d("Debug", "photoUri = $photoUri")
                        uploadPhoto(photoUri!!)
                        return@launch
                    }

                    if (response.body()?.email?.err != null || response.body()?.phone?.err != null) {
                        Log.d("mylog", response.body()?.email?.err.toString())
                        viewState.error(
                            response.body()?.email?.err ?: response.body()?.phone?.err.toString()
                        )
                        return@launch
                    }

                    viewState.success()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }
}