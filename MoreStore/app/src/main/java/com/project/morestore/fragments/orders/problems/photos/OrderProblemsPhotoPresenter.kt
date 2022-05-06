package com.project.morestore.fragments.orders.problems.photos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.project.morestore.R
import com.project.morestore.dialogs.FeedbackCompleteDialog
import com.project.morestore.models.FeedbackItem
import com.project.morestore.models.PhotoVideo
import com.project.morestore.models.ProductProblemsData
import com.project.morestore.repositories.OrdersRepository
import kotlinx.coroutines.*
import moxy.MvpPresenter
import moxy.presenterScope
import java.io.ByteArrayOutputStream

class OrderProblemsPhotoPresenter(val context: Context) : MvpPresenter<OrderProblemsPhotosView>() {

    private val ordersRepository = OrdersRepository(context)
    val photosList = ArrayList<Uri>()

    fun addPhoto(data: Uri) {
        photosList.add(data);
        viewState.showPhoto(data, photosList.size)
    }

    fun onNextClick(problemData: ProductProblemsData) {
        presenterScope.launch {
            val jobs = photosList.filterIsInstance<FeedbackItem.Photo>()
                .map { async { getBase64photos(it.photo) } }

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

    private suspend fun getBase64photos(uri: Uri): String = withContext(Dispatchers.IO) {
        val inStream = context.applicationContext.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        val photo64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        photo64
    }
}