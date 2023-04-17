package com.project.morestore.domain.presenters

import android.net.Uri
import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.presentation.mvpviews.BaseMvpView
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import java.io.File
import javax.inject.Inject

class MediaPresenter @Inject constructor(private val productRepository: ProductRepository): MvpPresenter<BaseMvpView>() {

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