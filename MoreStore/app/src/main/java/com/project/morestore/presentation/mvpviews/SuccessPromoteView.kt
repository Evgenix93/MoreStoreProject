package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

@OneExecution
interface SuccessPromoteView: MvpView {

 fun showPromoEndDate(date: String)
}