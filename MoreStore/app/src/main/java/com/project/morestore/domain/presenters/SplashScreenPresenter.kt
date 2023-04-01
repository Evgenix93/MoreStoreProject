package com.project.morestore.domain.presenters

import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.presentation.mvpviews.ResultLoadedMvpView
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class SplashScreenPresenter @Inject constructor(
    private val authRepository: AuthRepository
): MvpPresenter<ResultLoadedMvpView>() {

    fun checkToken() {
        presenterScope.launch {
            val token = authRepository.loadToken()
            val tokenSaveTime = authRepository.loadTokenSaveTime()
            val tokenExpiresTime = authRepository.loadTokenExpires()

            if (token == null || tokenSaveTime == null || tokenExpiresTime == null) {
                viewState.loaded(false)
                return@launch
            }

            val diffTime = System.currentTimeMillis() - tokenSaveTime
            val isExpired = (diffTime / 1000) / 60 > tokenExpiresTime
            if (isExpired) {
                viewState.loaded(false)
            } else {
                authRepository.setupToken(token)
                val response = authRepository.getUserData()
                response?.let { authRepository.setupUserId(it.body()?.id ?: 0) }
                viewState.loaded(true)
            }
        }
    }
}