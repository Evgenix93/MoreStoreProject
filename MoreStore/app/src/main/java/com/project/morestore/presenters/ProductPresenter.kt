package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.models.Size
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.repositories.ProductRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class ProductPresenter(context: Context): MvpPresenter<OnBoardingMvpView>() {
    private val repository = ProductRepository(context)

    fun getAllSizes(){
        presenterScope.launch {
            viewState.loading()
            val response = repository.getAllSizes()
            when(response?.code()){
                200 -> viewState.loaded(response.body()!!)
                400 -> viewState.error("ошибка")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

            }

        }

    }

    fun saveSizes(topSizes: List<Size>, bottomSizes: List<Size>, shoesSizes: List<Size>){
        presenterScope.launch {
            viewState.loading()
            if(repository.saveSizes(topSizes, bottomSizes, shoesSizes)){
                viewState.success()
            }else{
                viewState.error("ошибка")
            }
        }
    }


}