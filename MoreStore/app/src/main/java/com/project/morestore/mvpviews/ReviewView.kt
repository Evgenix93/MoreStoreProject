package com.project.morestore.mvpviews

import com.project.morestore.models.ReviewListItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface ReviewView :MvpView {
    fun showReviews(reviewItems :List<ReviewListItem.ReviewItem>)
}