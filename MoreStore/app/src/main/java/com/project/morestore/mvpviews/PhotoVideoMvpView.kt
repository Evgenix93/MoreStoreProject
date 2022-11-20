package com.project.morestore.mvpviews

import androidx.camera.video.Recording
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import java.io.File

interface PhotoVideoMvpView: PhotoMvpView {

    @OneExecution
    fun videoStarted(recording: Recording)

    @OneExecution
    fun videoEnded(file: File)

    @OneExecution
    fun error()

    @OneExecution
    fun videoError()
}