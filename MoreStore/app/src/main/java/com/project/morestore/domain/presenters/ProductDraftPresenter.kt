package com.project.morestore.domain.presenters

import com.project.morestore.data.models.Product
import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.presentation.mvpviews.ProductDraftView
import com.project.morestore.presentation.mvpviews.ResultLoadedMvpView
import com.project.morestore.util.errorMessage
import com.project.morestore.util.getStringFromResponse
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class ProductDraftPresenter(
    private val productRepository: ProductRepository
): MvpPresenter<ProductDraftView>() {

    fun getUserProductsWithStatus(status: Int) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCurrentUserProductsWithStatus(status)
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)

                404 -> {
                    viewState.loaded(emptyList<Product>())
                    viewState.error(response.errorBody()!!.getStringFromResponse())

                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }
}