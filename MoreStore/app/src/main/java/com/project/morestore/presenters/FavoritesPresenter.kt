package com.project.morestore.presenters

import android.content.Context
import android.util.Log
import com.project.morestore.models.*
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


    fun getProductWishList() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getProductWishList()
            when (response?.code()) {
                200 -> viewState.favoritesLoaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.emptyList()

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

            }
        }
    }

    fun getSellersWishList(){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getSellersWishList()
            when (response?.code()) {
                200 -> viewState.favoritesLoaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.emptyList()

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

            }

        }
    }

    fun getFavoriteSearches(){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getFavoriteSearches()
            when (response?.code()) {
                200 -> {
                    val searches = response.body()!!
                    /*val properties = getProperties()
                    searches.forEach { search ->
                        search.propertyValues = search.value.property.map { favoriteSearchProperty ->
                            properties.find { it.id == favoriteSearchProperty.idValue  }?.name.orEmpty()

                        }
                    }*/

                    viewState.favoritesLoaded(searches)

                }
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.emptyList()

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

            }

        }
    }

    fun getFavoriteSearchById(id: Long){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getFavoriteSearchById(id)
            when (response?.code()) {
                200 -> {
                    viewState.favoritesLoaded(listOf(response.body()!!))

                }
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.emptyList()

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

            }

        }
    }

    fun saveFavoriteSearch(filter: Filter) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.saveFavoriteSearch(FavoriteSearchValue(value = filter))
            when (response?.code()) {
                200 -> {
                    viewState.success()


                }
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.emptyList()
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
            }
        }
    }

    fun editFavoriteSearch(id: Long, filter: Filter){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.editFavoriteSearch(FavoriteSearchValue(id, filter))
            when (response?.code()) {
                200 -> {
                    viewState.success()


                }
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.emptyList()
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
            }

        }
    }

    fun deleteFavoriteSearch(id: Long){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.deleteFavoriteSearch(Id(id))
            when (response?.code()) {
                200 -> {
                    viewState.success()


                }
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.emptyList()
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
            }

        }
    }





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

    private suspend fun getProperties(): List<Property> {
        val response = productRepository.getProperties()
            when (response?.code()) {
                200 -> return response.body()!!
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                    return emptyList()
                }
                500 -> {
                    viewState.error("500 Internal Server Error")
                    return emptyList()
                }
                null -> {
                    viewState.error("нет интернета")
                    return emptyList()
                }
                else -> {
                    viewState.error("ошибка")
                    return emptyList()
                }

            }



    }

    fun reserveFilter(){
        userRepository.reserveFilter()

    }

    fun restoreFilter(){
        userRepository.restoreFilter()
    }

    fun getFilter(){
        viewState.favoritesLoaded(listOf(userRepository.getFilter()))
    }

    fun updateFilter(filter: Filter){
        userRepository.updateFilter(filter)
    }

    fun clearFilter(){
        userRepository.clearFilter()
    }


    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }


    }


}