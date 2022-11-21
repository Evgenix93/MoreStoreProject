package com.project.morestore.domain.presenters

import com.project.morestore.data.models.ReviewItem
import com.project.morestore.presentation.mvpviews.ReviewView
import com.project.morestore.data.repositories.OrdersRepository
import com.project.morestore.data.repositories.ReviewRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class ReviewsPresenter @Inject constructor(
    private val data :ReviewRepository,
    private val orderRepository: OrdersRepository
) :MvpPresenter<ReviewView>() {



     fun initReviews(userId: Long){
        presenterScope.launch {
            val response = orderRepository.getAllOrders()
            if (response?.code() == 200)
                viewState.showReviewButton(
                    response.body()!!.filter { it.status == 1 && it.cart != null }.find {
                        it.cart?.first()?.idUser == userId
                    } != null)
            else viewState.showReviewButton(false)
            viewState.showReviews(data.getReviews(userId).map { ReviewItem(it) })
        }

    }
}