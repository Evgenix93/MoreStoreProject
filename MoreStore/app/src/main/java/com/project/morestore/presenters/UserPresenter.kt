package com.project.morestore.presenters

import android.content.Context
import android.net.Uri
import android.util.Log
import com.project.morestore.models.Size
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.UserRepository
import com.project.morestore.util.isEmailValid
import com.project.morestore.util.isPhoneValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class UserPresenter(context: Context) : MvpPresenter<UserMvpView>() {
    private val repository = UserRepository(context)
    private val authRepository = AuthRepository(context)
    private var photoUri: Uri? = null

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
            val unmaskedPhone =  phone?.filter { it != '(' && it != ')' && it != '-' }
            if(email != null && !email.trim().isEmailValid()){
                viewState.error("почта указана неверно")
                return@launch
            }
            if(unmaskedPhone != null && !unmaskedPhone.trim().isPhoneValid()){
                viewState.error("телефон указан неверно")
                return@launch
            }
            if(step == null && phone == null && email == null && name.isNullOrBlank()){
                viewState.error("укажите имя")
                return@launch
            }
            if(step == null && phone == null && email == null && surname.isNullOrBlank()){
                viewState.error("укажите фамилию")
                return@launch
            }
            val response = repository.changeUserData(
                phone = unmaskedPhone?.trim(),
                email = email?.trim(),
                name = name,
                surname = surname,
                sex = sex,
                step = step,
                code = code
            )

            when(response?.code()){
                200 -> {
                    if (photoUri != null) {
                        Log.d("Debug", "photoUri = $photoUri")
                        uploadPhoto(photoUri!!)
                        return@launch
                    }

                    if (response.body()?.email?.err != null || response.body()?.phone?.err != null) {
                            Log.d("mylog", response.body()?.email?.err.toString())
                            viewState.error(response.body()?.email?.err ?: response.body()?.phone?.err.toString())
                            return@launch
                        }

                    viewState.success(Unit)
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


    fun changeUserData2(
        phone: String? = null,
        email: String? = null,
        step: Int? = null,
        code: Int? = null
    ) {
        presenterScope.launch {
            viewState.loading()
            val unmaskedPhone =  phone?.filter { it != '(' && it != ')' && it != '-' }
            if(email != null && !email.trim().isEmailValid()){
                viewState.error("почта указана неверно")
                return@launch
            }
            if(unmaskedPhone != null && !unmaskedPhone.trim().isPhoneValid()){
                viewState.error("телефон указан неверно")
                return@launch
            }
            val response = repository.changeUserData2(
                phone = unmaskedPhone?.trim(),
                email = email?.trim(),
                step = step,
                code = code
            )

            when(response?.code()){
                200 -> {
                    viewState.success(Unit)
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

    fun getNewCode(phone: String? = null, email: String? = null) {
        presenterScope.launch {
            Log.d("mylog", "getNewCode")
            viewState.loading()
            val response = authRepository.getNewCode(phone?.trim(), email?.trim())
            when (response?.code()) {
                200 -> {
                    viewState.successNewCode()
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

    fun safePhotoUri(uri: Uri) {
        photoUri = uri
    }


    private fun uploadPhoto(uri: Uri) {
        presenterScope.launch {
            val response = repository.uploadPhoto(uri)
            when (response?.code()) {
                200 -> {
                    viewState.success(Unit)
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


    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }


    }


    fun checkToken(){
        viewState.loaded(authRepository.checkToken())
    }

   fun clearToken(){
       authRepository.clearToken()
       viewState.success(Unit)
   }

}