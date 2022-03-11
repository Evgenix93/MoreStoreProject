package com.project.morestore.presenters

import com.project.morestore.models.ReviewListItem
import com.project.morestore.mvpviews.ReviewView
import com.project.morestore.repositories.ReviewRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class ReviewsPresenter(
    private val userId :Long,
    private val data :ReviewRepository
) :MvpPresenter<ReviewView>() {

    override fun attachView(view: ReviewView?) {
        super.attachView(view)
        presenterScope.launch {
            viewState.showReviews(data.getReviews(userId).map { ReviewListItem.Review(it) })
        }
    }
}