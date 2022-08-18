package com.project.morestore.presenters

import com.project.morestore.mvpviews.SuccessPromoteView
import moxy.MvpPresenter
import java.util.*

class SuccessPromotePresenter: MvpPresenter<SuccessPromoteView>() {

    fun getPromoEndDate(tariff: Int){
        val promoDays = if(tariff == 1) 1 else 7
        val promoEndDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + promoDays
        val calendar = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, promoEndDay) }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayStr = if(day < 10) "0$day" else day.toString()
        val month = calendar.get(Calendar.MONTH)
        val monthStr = if (month < 10) "0$month" else month.toString()
        val year = calendar.get(Calendar.YEAR)
        val dateString = "$dayStr.$monthStr.$year"
        viewState.showPromoEndDate(dateString)
    }
}