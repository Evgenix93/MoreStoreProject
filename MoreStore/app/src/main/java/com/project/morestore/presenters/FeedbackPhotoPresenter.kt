package com.project.morestore.presenters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.project.morestore.models.Feedback
import com.project.morestore.models.FeedbackItem
import com.project.morestore.mvpviews.FeedbackPhotoView
import com.project.morestore.repositories.ReviewRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class FeedbackPhotoPresenter @Inject constructor(
    @ApplicationContext private val applicationContext :Context,
    private val data: ReviewRepository
) :MvpPresenter<FeedbackPhotoView>() {

    private var photos = mutableListOf(FeedbackItem.AddPhoto(), FeedbackItem.Description())

    fun addPhoto(uris :List<Uri>, isChat: Boolean){
        photos.filterIsInstance<FeedbackItem.Photo>()
            .also {
                photos = when(it.size){
                    0 -> (uris.map { uri -> FeedbackItem.Photo(uri)} + listOf(FeedbackItem.AddPhoto(isChat))).toMutableList()
                    in 1..3 -> (it + uris.map { uri -> FeedbackItem.Photo(uri)} + FeedbackItem.AddPhoto(isChat)).toMutableList()
                    else -> (it + uris.map {uri ->FeedbackItem.Photo(uri)}).toMutableList()
                }
            }
        if(photos.filterIsInstance<FeedbackItem.Photo>().size == 1) viewState.changeSendText()
        viewState.showPhotos(photos)
    }

    fun createFeedback(productId: Long, rate: Byte, feedback: String){
        presenterScope.launch {
            val jobs = photos.filterIsInstance<FeedbackItem.Photo>()
                .map { async { getBase64photos(it.photo) } }
            data.createReview(Feedback(productId, rate, feedback), jobs.awaitAll())
            viewState.showSuccess(rate < 4)
        }
    }

    private suspend fun getBase64photos(uri :Uri) :String = withContext(IO){
        val inStream = applicationContext.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        val photo64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        photo64
    }

   fun saveMediaUris(uris: List<Uri>){
       data.saveMediaUris(uris)
       viewState.mediaUrisSaved()
   }

}