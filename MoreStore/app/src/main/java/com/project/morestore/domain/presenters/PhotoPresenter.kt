package com.project.morestore.domain.presenters

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.project.morestore.data.repositories.PhotoVideoRepository
import com.project.morestore.presentation.mvpviews.PhotoMvpView
import dagger.hilt.android.qualifiers.ApplicationContext
import moxy.MvpPresenter
import javax.inject.Inject

class PhotoPresenter @Inject constructor(@ApplicationContext private val context: Context,
                                         private val repository: PhotoVideoRepository): MvpPresenter<PhotoMvpView>() {

    fun takePhoto(imageCapture: ImageCapture){

        val file = repository.createFileForPhoto()
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(file)
            .build()


        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    viewState.onPhotoCaptured(file)
                }
            }
        )
    }
}