package com.project.morestore.mvpviews

import com.project.morestore.data.models.User
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface WaitReviewSellersMvpView: MvpView {

  @OneExecution
  fun  onSellersLoaded(sellers: Set<User>)

  @OneExecution
  fun onError(message: String)
}