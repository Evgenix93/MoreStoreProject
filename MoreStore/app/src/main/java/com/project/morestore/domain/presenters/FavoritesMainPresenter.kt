package com.project.morestore.domain.presenters

import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.presentation.mvpviews.FavoritesMainMvpView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class FavoritesMainPresenter @Inject constructor(private val userRepository: UserRepository, private val authRepository: AuthRepository)
    : MvpPresenter<FavoritesMainMvpView>() {

    fun getProductWishList() {
        presenterScope.launch {

            val response = userRepository.getProductWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)

            }
        }
    }

    fun getBrandWishList() {
        presenterScope.launch {
            val response = userRepository.getBrandWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)

            }
        }
    }

    fun getSellersWishList(){
        presenterScope.launch {

            val response = userRepository.getSellersWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
            }
        }
    }

    fun getFavoriteSearches(){
        presenterScope.launch {

            val response = userRepository.getFavoriteSearches()
            when (response?.code()) {
                200 -> {
                    val searches = response.body()!!
                    viewState.loaded(searches)
                }
            }
        }
    }

    fun tokenCheck() {
        if(authRepository.isTokenEmpty())
            viewState.isGuest()
    }
}