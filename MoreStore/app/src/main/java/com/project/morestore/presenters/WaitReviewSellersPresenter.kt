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
              200 -> viewState.onSellersLoaded(response.body()!!.toSet())
              404 -> viewState.onError("Список пуст")
              500 -> viewState.onError("500 Internal Server Error")
              null -> viewState.onError("Нет сети")
          }
        }
    }
}