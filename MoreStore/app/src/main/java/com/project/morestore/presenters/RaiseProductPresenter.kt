package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.models.PaymentUrl
import com.project.morestore.models.Tariff
import com.project.morestore.mvpviews.RaiseProductView
import com.project.morestore.repositories.ProductRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class RaiseProductPresenter(context: Context): MvpPresenter<RaiseProductView>() {
    private val productRepository = ProductRepository(context)

    fun raiseProduct(page: Int, idProduct: Long){
      presenterScope.launch{
         // viewState.loading(true)
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

              else -> {viewState.error(response.errorBody()?.string().toString())}
          }
      }
    }
}