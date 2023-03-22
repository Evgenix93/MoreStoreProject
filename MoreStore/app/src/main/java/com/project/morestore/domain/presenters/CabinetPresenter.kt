package com.project.morestore.domain.presenters

import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.presentation.mvpviews.CabinetMvpView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class CabinetPresenter @Inject constructor(private val userRepository: UserRepository,
                                           private val authRepository: AuthRepository,
                                           private val productRepository: ProductRepository): MvpPresenter<CabinetMvpView>() {


    fun checkToken() {
        viewState.isLoggedIn(authRepository.isTokenEmpty().not())
    }

     fun getUserInfo() {
        presenterScope.launch {
            viewState.loading(true)
            val response = userRepository.getCurrentUserInfo()
            when (response?.code()) {
                200 -> {
                    viewState.loading(false)
                    viewState.currentUserLoaded(response.body()!!)
                }
                else -> {
                    viewState.loading(false)
                    viewState.error(errorMessage(response))
                }
            }
        }
    }

    fun getUserProductsCounts(){
        presenterScope.launch {
            val response = productRepository.getCurrentUserProducts()
            when (response?.code()) {
                  200 -> {
                      response.body()?.forEach {
                          val status = when {
                              it.statusUser?.order?.status == 1 -> 8
                              it.statusUser?.order?.status == 0 -> 6
                              else -> it.status
                          }
                          it.status = status
                      }
                      viewState.showProductsCounts(
                          listOf(
                              response.body()!!
                                  .filter { it.status == 1 || it.status == 6 }.size,
                              response.body()!!.filter { it.status == 0 }.size,
                              response.body()!!.filter { it.status == 8 }.size,
                              response.body()!!.filter { it.status == 5 }.size

                          )
                      )
                  }
                else -> {}
            }
        }
    }




}