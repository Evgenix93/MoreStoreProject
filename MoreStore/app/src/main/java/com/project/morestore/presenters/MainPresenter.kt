package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ProductRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class MainPresenter(context: Context): MvpPresenter<MainMvpView>() {
    private val authRepository = AuthRepository(context)
    private val productRepository = ProductRepository(context)

    fun loadOnBoardingViewed() {
        presenterScope.launch {
            viewState.loading()
            if(productRepository.loadOnBoardingViewed()){
                viewState.loaded(Unit)
            }else{
                if(!authRepository.isTokenEmpty()) {
                    viewState.showOnBoarding()
                }else{
                    viewState.loaded(Unit)
                }
            }
        }
    }

  fun loadFilter(){
      presenterScope.launch{
          productRepository.loadFilter()
      }
  }

}