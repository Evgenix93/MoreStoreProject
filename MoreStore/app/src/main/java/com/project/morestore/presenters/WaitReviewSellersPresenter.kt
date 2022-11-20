package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.mvpviews.WaitReviewSellersMvpView
import com.project.morestore.repositories.UserRepository
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