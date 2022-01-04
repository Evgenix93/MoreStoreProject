package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.SingleState


@OneExecution
interface AuthMvpView: MvpView {

     fun success(response: Any)
     fun loading()

     fun error(message: String)

}