package com.project.morestore.presenters

import android.content.Context
import android.util.Log

import com.project.morestore.models.Size
import com.project.morestore.mvpviews.OnBoardingMvpView



import com.project.morestore.repositories.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody


class ProductPresenter(context: Context): MvpPresenter<OnBoardingMvpView>() {
    private val repository = ProductRepository(context)
    private val categoryIdList = mutableListOf<Int>()

    fun getAllSizes(){
        presenterScope.launch {
            viewState.loading()
            val response = repository.getAllSizes()
            when(response?.code()){
                200 -> viewState.loaded(response.body()!!)
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
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






    fun getCategories(){
        presenterScope.launch {
            val response = repository.getCategories()
            when(response?.code()){
                200 -> viewState.loaded(response.body()!!)
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("Нет интернета")
                else -> viewState.error("Ошибка")
            }
        }
    }

    fun safeCategories(){
        presenterScope.launch {
           if (repository.safeCategories(categoryIdList))
               viewState.success()
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


    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }


    }



}