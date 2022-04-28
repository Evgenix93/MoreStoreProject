package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.mvpviews.WaitReviewSellersMvpView
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class WaitReviewSellersPresenter(context: Context): MvpPresenter<WaitReviewSellersMvpView>() {
    private val repository = UserRepository(context)

    fun getWaitReviewSellers(){
        presenterScope.launch {
          val response = repository.getWaitReviewSellers()
          when(response?.code()){
              200 -> viewState.onSellersLoaded(response.body()!!)
              null -> viewState.onError("Ошибка")
          }
        }
    }
}