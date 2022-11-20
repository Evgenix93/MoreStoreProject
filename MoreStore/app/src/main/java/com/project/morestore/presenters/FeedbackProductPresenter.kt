package com.project.morestore.presenters

import com.project.morestore.mvpviews.FeedbackProductView
import com.project.morestore.repositories.ReviewRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class FeedbackProductPresenter @Inject constructor(
    private val data: ReviewRepository
) :MvpPresenter<FeedbackProductView>(){

    fun getProducts(userId: Long) {
        presenterScope.launch {
            viewState.showProducts(data.getUserProducts(userId).toList())
        }
    }
}