package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import java.io.File

interface PhotoMvpView: MvpView {

    @OneExecution
    fun onPhotoCaptured(file: File)
}