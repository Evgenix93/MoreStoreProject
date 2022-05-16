package com.project.morestore.fragments.orders.problems.photos

import android.net.Uri
import com.project.morestore.dialogs.FeedbackCompleteDialog
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import java.io.File

interface OrderProblemsPhotosView : MvpView {
    @AddToEnd
    fun showPhoto(data: File, position: Int)
    @OneExecution
    fun navigateBack()
    @OneExecution
    fun showCompleteDialog(dialog: FeedbackCompleteDialog)
}