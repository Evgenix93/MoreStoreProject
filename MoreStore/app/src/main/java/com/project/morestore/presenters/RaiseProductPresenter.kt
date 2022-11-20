package com.project.morestore.presenters

import com.project.morestore.data.models.Tariff
import com.project.morestore.mvpviews.RaiseProductView
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class RaiseProductPresenter @Inject constructor(private val productRepository: ProductRepository): MvpPresenter<RaiseProductView>() {


    fun raiseProduct(page: Int, idProduct: Long){
      presenterScope.launch{

          val response = productRepository.raiseProduct(
              if (page == 0)
                  Tariff("buyTariff", idProduct, 1)
              else
                  Tariff("buyTariff", idProduct, 2)
          )
          when(response.code()){
              200 -> {
                  viewState.payment(response.body()!!)
              }

              else -> viewState.error(errorMessage(response))
          }
      }
    }
}