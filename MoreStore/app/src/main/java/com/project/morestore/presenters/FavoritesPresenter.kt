package com.project.morestore.presenters

import android.content.Context
import android.util.Log
import com.project.morestore.models.Filter
import com.project.morestore.models.Product
import com.project.morestore.models.ProductBrand
import com.project.morestore.mvpviews.FavoritesMvpView
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class FavoritesPresenter(context: Context): MvpPresenter<FavoritesMvpView>() {
    private val userRepository = UserRepository(context)
    private val productRepository = ProductRepository(context)

    fun getBrandWishList() {
        presenterScope.launch {
            val response = userRepository.getBrandWishList()
            when (response?.code()) {
                200 -> viewState.favoritesLoaded(response.body()!!)
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                404 -> {
                    viewState.error("Список пуст")
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")
            }
        }
    }

    fun getProducts(brands: List<ProductBrand>){
        presenterScope.launch {
          brands.forEach {
              it.isChecked = true
              it.isWished = true
          }
          val filter = userRepository.getFilter()
          val response = productRepository.getProducts(filter = Filter(chosenForWho = filter.chosenForWho, brands = brands, status = 1))
            when (response?.code()) {
                200 -> viewState.favoritesLoaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.favoritesLoaded(emptyList<Product>())
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

            }
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