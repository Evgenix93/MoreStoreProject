package com.project.morestore.presenters

import com.project.morestore.mvpviews.FeedbackProductView
import com.project.morestore.repositories.ReviewRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class FeedbackProductPresenter(
    private val userId :Long,
    private val data :ReviewRepository
) :MvpPresenter<FeedbackProductView>(){

    init {
        presenterScope.launch {
            viewState.showProducts(data.getUserProducts(userId).toList())
        }
    }
}