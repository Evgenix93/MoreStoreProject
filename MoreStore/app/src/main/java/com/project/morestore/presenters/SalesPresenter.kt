package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.models.Order
import com.project.morestore.models.User
import com.project.morestore.mvpviews.SalesMvpView
import com.project.morestore.repositories.SalesRepository
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class SalesPresenter(context: Context): MvpPresenter<SalesMvpView>() {
    private val repository = SalesRepository()
    private val userRepository = UserRepository(context)
    private lateinit var sales: List<Order>

    fun getSales(){
        presenterScope.launch {
            val response = repository.getSales()
            when(response?.code()){
                200 -> {

                }
                400 -> {viewState.onError(getError(response.errorBody()!!))}
                0 -> {viewState.onError(getError(response.errorBody()!!))}
                null -> {viewState.onError("Нет интернета")}
            }
        }
    }

    fun addDealPlace(orderId: Long, address: String){
        presenterScope.launch{
            val response = repository.addDealPlace(orderId, address)
            when(response?.code()){
                200 -> {
                    if(response.body()!!)
                    viewState.onDealPlaceAdded()
                    else
                        viewState.onError("Ошибка при добавлении адреса")
                }
                400 -> {viewState.onError(getError(response.errorBody()!!))}
                null -> {viewState.onError("Нет интернета")}
            }
        }
        }

   private suspend fun getUser(id: Long){
          val response = userRepository.getUser(id)
            when(response?.code()){
                200 -> {
                    viewState.onUserLoaded()
                }
                400 -> {viewState.onError(getError(response.errorBody()!!))}
                0 -> {viewState.onError(getError(response.errorBody()!!))}
                null -> {viewState.onError("Нет интернета")}
            }
        }

  private suspend fun getError(errorBody: ResponseBody): String{
      return  withContext(Dispatchers.IO){
            errorBody.string()
        }
    }
}