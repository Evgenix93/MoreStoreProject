package com.project.morestore.presenters

import android.content.Context
import android.util.Log
import com.project.morestore.models.*
import com.project.morestore.models.Filter
import com.project.morestore.models.Product
import com.project.morestore.models.ProductBrand
import com.project.morestore.mvpviews.FavoritesMainMvpView
import com.project.morestore.mvpviews.FavoritesMvpView
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody
import javax.inject.Inject

class FavoritesPresenter @Inject constructor(
    private val userRepository: UserRepository,
private val productRepository: ProductRepository,
private val authRepository: AuthRepository): MvpPresenter<MainMvpView>() {

    fun getProductWishList() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getProductWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()

                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun tokenCheck() {
        if(authRepository.isTokenEmpty())
            (viewState as FavoritesMainMvpView).isGuest()
    }

    fun getSellersWishList(){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getSellersWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()

                }
                else -> viewState.error(errorMessage(response))
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
                    viewState.loaded(searches)
                }
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()

                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getFavoriteSearchById(id: Long){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getFavoriteSearchById(id)
            when (response?.code()) {
                200 -> {
                    viewState.loaded(listOf(response.body()!!))

                }
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()

                }
                else -> viewState.error(errorMessage(response))
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
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()
                }
                else -> viewState.error(errorMessage(response))
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
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()
                }
                else -> viewState.error(errorMessage(response))
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
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()
                }
                else -> viewState.error(errorMessage(response))
            }

        }
    }

    fun getBrandWishList() {
        presenterScope.launch {
            val response = userRepository.getBrandWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                404 -> {
                    (viewState as FavoritesMvpView).emptyList()
                }
                else -> viewState.error(errorMessage(response))
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
                200 -> viewState.loaded(response.body()!!)
                404 -> {
                    viewState.loaded(emptyList<Product>())
                }
                else -> viewState.error(errorMessage(response))
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
        viewState.loaded(listOf(userRepository.getFilter()))
    }

    fun updateFilter(filter: Filter){
        userRepository.updateFilter(filter)
    }
}