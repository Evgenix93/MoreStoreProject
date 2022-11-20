package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.models.ReviewItem
import com.project.morestore.mvpviews.ReviewView
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.ReviewRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class ReviewsPresenter(
    private val userId :Long,
    private val data :ReviewRepository,
    context: Context
) :MvpPresenter<ReviewView>() {
    private val orderRepository = OrdersRepository()

    override fun attachView(view: ReviewView?) {
        super.attachView(view)
        presenterScope.launch {
            initReviewButton(userId)
            viewState.showReviews(data.getReviews(userId).map { ReviewItem(it) })

        }
    }

    private suspend fun initReviewButton(userId: Long){
        val response = orderRepository.getAllOrders()
            if(response?.code() == 200)
                viewState.showReviewButton(response.body()!!.filter { it.status == 1 && it.cart != null }.find {
                    it.cart?.first()?.idUser == userId
                } != null)
            else viewState.showReviewButton(false)

    }
}