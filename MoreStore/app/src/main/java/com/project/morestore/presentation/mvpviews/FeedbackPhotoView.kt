package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.FeedbackItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface FeedbackPhotoView :MvpView {
    fun showPhotos(items :List<FeedbackItem>)
    fun changeSendText()
    fun showSuccess(review :Boolean)
    fun mediaUrisSaved()
}