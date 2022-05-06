package com.project.morestore.fragments.orders.problems

import com.project.morestore.dialogs.ProblemTypeDialog
import com.project.morestore.models.ProductProblemsData
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface OrderProblemsView : MvpView {
    @OneExecution
    fun openProblemsPhotosPage(productProblemsData: ProductProblemsData)
    @AddToEndSingle
    fun setProductData(productProblemsData: ProductProblemsData)
    @OneExecution
    fun showReasonDialog(problemDialog: ProblemTypeDialog)
    @AddToEndSingle
    fun setPhone(phone: String)
}