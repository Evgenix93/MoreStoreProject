package com.project.morestore.presenters.toolbar.cart

import com.project.morestore.adapters.SliderMenuAdapter
import com.project.morestore.models.slidermenu.OrdersSliderMenu
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution


interface ToolbarCartView : MvpView {
    @AddToEndSingle
    fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>)
    @OneExecution
    fun navigate(pageId: Int?)
}