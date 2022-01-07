package com.project.morestore.presenters

import android.content.Context

import com.project.morestore.models.Size
import com.project.morestore.mvpviews.OnBoardingMvpView

import com.project.morestore.mvpviews.OnboardingMvpView

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




    private val categoryIdList = mutableListOf<Int>()

    fun getCategories(){
        presenterScope.launch {
            val response = repository.getCategories()
            when(response?.code()){
                200 -> viewState.loaded(response.body()!!)
                400 -> viewState.error("Ошибка")
                null -> viewState.error("Нет интернета")
                else -> viewState.error("Ошибка")
            }
        }
    }

    fun safeCategories(){
        presenterScope.launch {
           if (repository.safeCategories(categoryIdList))
               viewState.success(Unit)
            else
                viewState.error("Ошибка")
        }
    }

    fun addRemoveCategoryId(id: Int, isChecked: Boolean){
       if (isChecked)
          categoryIdList.add(id)
       else
           categoryIdList.remove(id)
    }



}