package com.project.morestore.mvpviews

import com.project.morestore.models.FeedbackItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface FeedbackPhotoView :MvpView {
    fun showPhotos(items :List<FeedbackItem>)
    fun changeSendText()
    fun showSuccess(review :Boolean)
}