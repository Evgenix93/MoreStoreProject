package com.project.morestore.presenters

import android.content.Context
import android.util.Log


import com.project.morestore.models.Size
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.repositories.AuthRepository


import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import com.project.morestore.singletones.FilterState
import com.project.morestore.singletones.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException


class OnboardingPresenter(context: Context): MvpPresenter<OnBoardingMvpView>() {
    private val repository = ProductRepository(context)
    private val authRepository = AuthRepository(context)
    private val userRepository = UserRepository(context)

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

    fun getProperties(propertyId: Long){
        presenterScope.launch {
            viewState.loading()
            val response = repository.getProperties()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!.filter { it.idCategory == propertyId })
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

    fun getTopSizes(){
        getProperties(4)
    }

    fun getBottomSizes(){
        getProperties(5)
    }

    fun getShoosSizes(){
        getProperties(6)
    }

    fun saveSizes(topSizes: List<Size>, bottomSizes: List<Size>, shoesSizes: List<Size>){
        presenterScope.launch {
            viewState.loading()
            /*if(repository.saveSizes(topSizes, bottomSizes, shoesSizes)){
                viewState.success()
            }else{
                viewState.error("ошибка")
            }*/
            repository.saveSizes(topSizes, bottomSizes, shoesSizes)
            viewState.success()
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

    fun safeCategories(segmentsChecked: List<Boolean>){
       /* presenterScope.launch {
           if (repository.safeCategories(categoryIdList))
               viewState.success()
            else
                viewState.error("Ошибка")
        }*/
        repository.safeCategories(segmentsChecked)
        viewState.success()
    }

    fun addRemoveCategoryId(id: Int, isChecked: Boolean){
       if (isChecked)
          categoryIdList.add(id)
       else
           categoryIdList.remove(id)
    }

    fun saveOnBoardingViewed(){
        presenterScope.launch {
            repository.saveOnBoardingViewed()
        }
    }

    fun changeToGuestMode(){
        authRepository.clearToken()
    }

   fun safeFilter(){
       presenterScope.launch{
           if (userRepository.saveFilter())
               viewState.success()
           else
               viewState.error("Ошибка")
       }
   }


    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }


    }



}