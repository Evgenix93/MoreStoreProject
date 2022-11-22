package com.project.morestore.domain.presenters

import com.project.morestore.data.models.*
import com.project.morestore.data.models.Filter
import com.project.morestore.data.models.Product
import com.project.morestore.data.models.ProductBrand
import com.project.morestore.presentation.mvpviews.FavoritesMainMvpView
import com.project.morestore.presentation.mvpviews.FavoritesMvpView
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class FavoritesPresenter @Inject constructor(
    private val userRepository: UserRepository,
private val productRepository: ProductRepository): MvpPresenter<FavoritesMvpView>() {

    fun getProductWishList() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getProductWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                404 -> {
                    viewState.emptyList()

                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }



    fun getSellersWishList(){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getSellersWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                404 -> {
                    viewState.emptyList()

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
                    viewState.emptyList()

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
                    viewState.emptyList()
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

    fun getFilter(){
        viewState.loaded(listOf(userRepository.getFilter()))
    }


}