package com.project.morestore.domain.presenters

import android.graphics.Bitmap
import android.net.Uri
import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.presentation.mvpviews.PhotoFinishMvpView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import java.io.File
import javax.inject.Inject

class PhotoFinishPresenter @Inject constructor(private val productRepository: ProductRepository): MvpPresenter<PhotoFinishMvpView>() {
    fun updateCreateProductDataPhotosVideos(photoVideoUri: Uri, position: Int) {
        presenterScope.launch {
            val success = productRepository.updateCreateProductPhotoVideo(photoVideoUri, position)
            if (success)
                viewState.success()
            else viewState.error("ошибка")

        }
    }

    fun updateCreateProductDataPhotosVideos(photoVideo: File, position: Int) {
        productRepository.updateCreateProductPhotoVideo(photoVideo, position)
        viewState.success()
    }

    fun updateCreateProductDataPhotosVideos(bitmap: Bitmap, position: Int) {
        presenterScope.launch {
            val success = productRepository.updateCreateProductPhotoVideo(bitmap, position)
            if (success)
                viewState.success()
            else viewState.error("ошибка")
        }
    }

    fun deletePhotoBackground(file: File? = null, uri: Uri? = null) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.deletePhotoBackground(file, uri)
            when (response?.code()) {
                200 -> {
                    viewState.loaded(response.body()!!.first())

                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun playVideo(fileUri: Uri? = null, file: File? = null) {
        presenterScope.launch {
            viewState.loading()
            val intent = productRepository.getPlayVideoIntent(fileUri, file)
            if (intent == null) {
                viewState.error("ошибка")
                return@launch
            }
            viewState.loaded(intent)
        }
    }
}