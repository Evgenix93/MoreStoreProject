package com.project.morestore.presenters

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.project.morestore.mvpviews.PhotoVideoMvpView
import com.project.morestore.repositories.PhotoVideoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import java.io.File

class PhotoVideoPresenter(val context: Context): MvpPresenter<PhotoVideoMvpView>() {
    private val repository = PhotoVideoRepository(context)
    private var pressedTime: Long = 0
    private var prepareVideoJob: Job? = null


    fun takePhoto(imageCapture: ImageCapture){

        // Create output options object which contains file + metadata
        val file = repository.createFileForPhoto()
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(file)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("mylog", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    viewState.onPhotoCaptured(file)
                    Log.d("mylog", msg)
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
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Log.d("mylog", msg)
                            viewState.videoEnded(file)
                        } else {
                            Log.e("mylog", "Video capture ends with error: " +
                                    "${recordEvent.error}")
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
           // takePhoto(photoCapture)
            presenterScope.launch{
               val file = repository.createFileForPhoto(bitmap)
               if(file != null)
              viewState.onPhotoCaptured(file)
            }
        }
    }

    fun playVideo(videoUri: Uri){

    }

}
