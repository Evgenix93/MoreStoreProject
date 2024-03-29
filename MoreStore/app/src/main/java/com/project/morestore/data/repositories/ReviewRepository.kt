package com.project.morestore.data.repositories

import android.net.Uri
import com.project.morestore.data.apis.ReviewApi
import com.project.morestore.data.models.Feedback
import com.project.morestore.data.models.FeedbackProduct
import com.project.morestore.data.models.PhotoVideo
import com.project.morestore.data.models.PhotoData
import com.project.morestore.data.singletones.ChatMedia
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class ReviewRepository @Inject constructor(private val network: ReviewApi) {


    suspend fun getReviews(userId :Long) = withContext(IO){
        try{
            network.getUserReviews(userId)
        } catch (e :HttpException){
            e.printStackTrace()
            arrayOf()
        }
    }

    suspend fun getUserProducts(userId :Long) :List<FeedbackProduct> = withContext(IO){
        try{
            network.getUserProducts(userId).toList()
        } catch (e :HttpException){
            e.printStackTrace()
            listOf()
        }
    }

    suspend fun createReview(feedback :Feedback, photos :List<String>) = withContext(IO){
        try{
            val id = network.createReview(feedback).id
            network.uploadPhoto(
                PhotoData("ReviewPhoto", id, photos.map { PhotoVideo("jpg", it) })
            )
        } catch (e :HttpException){
            e.printStackTrace()
        }
    }

    fun saveMediaUris(uris: List<Uri>){
        ChatMedia.mediaUris = uris
    }
}