package com.project.morestore.presentation.mvpviews

import androidx.camera.video.Recording
import moxy.viewstate.strategy.alias.OneExecution
import java.io.File

interface PhotoVideoMvpView: PhotoMvpView {

    @OneExecution
    fun videoStarted(recording: Recording)

    @OneExecution
    fun videoEnded(file: File)



    @OneExecution
    fun videoError()
}