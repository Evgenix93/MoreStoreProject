package com.project.morestore.presentation.mvpviews

import com.project.morestore.presentation.adapters.SliderMenuAdapter
import com.project.morestore.data.models.slidermenu.OrdersSliderMenu
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution


interface ToolbarCartView : MvpView {
    @AddToEndSingle
    fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>)
    @OneExecution
    fun navigate(pageId: Int?)
}