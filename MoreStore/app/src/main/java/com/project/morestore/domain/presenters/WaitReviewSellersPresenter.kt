package com.project.morestore.domain.presenters

import com.project.morestore.presentation.mvpviews.WaitReviewSellersMvpView
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class WaitReviewSellersPresenter @Inject constructor(private val repository: UserRepository): MvpPresenter<WaitReviewSellersMvpView>() {

    fun getWaitReviewSellers(){
        presenterScope.launch {
          val response = repository.getWaitReviewSellers()
          when(response?.code()){
              200 -> viewState.onSellersLoaded(response.body()!!.toSet())
              404 -> viewState.onError("Список пуст")
              else -> viewState.onError(errorMessage(response))
          }
        }
    }
}