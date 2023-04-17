package com.project.morestore.domain.presenters

import com.project.morestore.data.repositories.SalesRepository
import com.project.morestore.presentation.mvpviews.DealPlaceMvpView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class DealPlacePresenter @Inject constructor(private val salesRepository: SalesRepository): MvpPresenter<DealPlaceMvpView>() {

    fun addDealPlace(orderId: Long, address: String){
        presenterScope.launch{
            val response = salesRepository.addDealPlace(orderId, address)
            when(response?.code()){
                200 -> {
                    if(response.body()!!)
                        (viewState as DealPlaceMvpView).onDealPlaceAdded()
                    else
                        viewState.error("Ошибка при добавлении адреса")
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }
}