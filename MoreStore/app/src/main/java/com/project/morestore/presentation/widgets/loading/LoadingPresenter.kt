package com.project.morestore.presentation.widgets.loading

import moxy.MvpPresenter
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface LoadingView : MvpView {
    @AddToEndSingle fun showLoading(show :Boolean = true)
}

class LoadingPresenter :MvpPresenter<LoadingView>() {

    fun show(){
        viewState.showLoading()
    }

    fun hide(){
        viewState.showLoading(false)
    }
}