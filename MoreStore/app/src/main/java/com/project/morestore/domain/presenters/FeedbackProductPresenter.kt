package com.project.morestore.domain.presenters

import com.project.morestore.presentation.mvpviews.FeedbackProductView
import com.project.morestore.data.repositories.ReviewRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class FeedbackProductPresenter @Inject constructor(
    private val data: ReviewRepository
) :MvpPresenter<FeedbackProductView>(){

    fun getProducts(userId: Long?) {
        userId ?: return
        presenterScope.launch {
            viewState.showProducts(data.getUserProducts(userId).toList())
        }
    }
}