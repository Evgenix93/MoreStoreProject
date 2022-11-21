package com.project.morestore.domain.presenters

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.project.morestore.presentation.mvpviews.PhotoVideoMvpView
import com.project.morestore.data.repositories.PhotoVideoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class PhotoVideoPresenter @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: PhotoVideoRepository
): MvpPresenter<PhotoVideoMvpView>() {

    private var pressedTime: Long = 0
    private var prepareVideoJob: Job? = null

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


    fun takeVideo(videoCapture: VideoCapture<Recorder>){

        val file = repository.createFileForVideo()

        val fileOutputOptions = FileOutputOptions.Builder(file).build()

        val recording = videoCapture.output
            .prepareRecording(context, fileOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {}
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            viewState.videoEnded(file)
                        } else {
                            viewState.videoError()
                        }
                        }
                    }
                }
        viewState.videoStarted(recording)
            }


    fun photoVideoBtnPressed(videoCapture: VideoCapture<Recorder>){
        pressedTime = System.currentTimeMillis()
        prepareVideoJob = presenterScope.launch {
            delay(1000)
            takeVideo(videoCapture)
        }

    }

    fun photoVideoBtnReleased(bitmap: Bitmap){
        val timeDiff = System.currentTimeMillis() - pressedTime
        if(timeDiff/1000 < 1){
            prepareVideoJob?.cancel()
            presenterScope.launch{
               val file = repository.createFileForPhoto(bitmap)
               if(file != null)
              viewState.onPhotoCaptured(file)
            }
        }
    }

}
