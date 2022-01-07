package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.mvpviews.OnboardingMvpView
import com.project.morestore.repositories.ProductRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class ProductPresenter(context: Context): MvpPresenter<OnboardingMvpView>() {
    private val repository = ProductRepository(context)
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