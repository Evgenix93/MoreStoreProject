package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.ReviewListItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface ReviewView :MvpView {
    fun showReviews(reviewItems :List<ReviewListItem>)
    fun showReviewButton(show: Boolean)
}