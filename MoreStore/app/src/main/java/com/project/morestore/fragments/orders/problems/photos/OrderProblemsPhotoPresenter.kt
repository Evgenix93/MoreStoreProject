package com.project.morestore.fragments.orders.problems.photos

import android.content.Context
import android.util.Base64
import com.project.morestore.R
import com.project.morestore.dialogs.FeedbackCompleteDialog
import com.project.morestore.models.PhotoVideo
import com.project.morestore.models.ProductProblemsData
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.ProductRepository
import kotlinx.coroutines.*
import moxy.MvpPresenter
import moxy.presenterScope
import java.io.File
import javax.inject.Inject

class OrderProblemsPhotoPresenter @Inject constructor(val context: Context,
                                  private val ordersRepository: OrdersRepository,
                                  private val productRepository: ProductRepository) : MvpPresenter<OrderProblemsPhotosView>() {


    var photosList = listOf<File>()

    fun onNextClick(problemData: ProductProblemsData) {
        presenterScope.launch {
            val jobs = //photosList.filterIsInstance<FeedbackItem.Photo>()
                photosList.map { async { getBase64photos(it) } }

            problemData.photo = jobs.awaitAll().map { PhotoVideo("jpg", it) }
            ordersRepository.orderProblem(problemData)

            val dialog = FeedbackCompleteDialog(
                context,
                {
                    viewState.navigateBack()
                },
                context.getString(R.string.order_problem_photos_dialog_title),
                context.getString(R.string.order_problem_photos_dialog_content)
            )

            viewState.showCompleteDialog(dialog)
        }
    }

    private suspend fun getBase64photos(file: File): String = withContext(Dispatchers.IO) {
        val photo64 = Base64.encodeToString(file.readBytes(), Base64.DEFAULT)
        photo64
    }

     fun getPhotos(){
         photosList = productRepository.loadCreateProductPhotosVideos().values.toList()
         photosList.forEachIndexed { index, file -> viewState.showPhoto(file, index + 1)  }

    }
}