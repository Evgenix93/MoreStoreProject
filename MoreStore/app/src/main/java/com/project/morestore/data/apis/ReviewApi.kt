package com.project.morestore.data.apis

import com.project.morestore.data.models.*
import retrofit2.http.*

interface ReviewApi {

    @GET("review/{id}/id_product")
    suspend fun getProductReviews(
        @Path("id") productId :Long
    ) :Array<Review>

    @GET("review/0/id_user")
    suspend fun getUserReviews(
        @Query("id_user") userId: Long
    ) :Array<Review>

    @POST("review")
    suspend fun createReview(
        @Body feedback: Feedback
    ) :Id

//    @POST("review/delete")
//    suspend fun deleteReview(
//        @Body reviewId :Id
//    ) :Id//?????

    @POST("upload/photo")
    suspend fun uploadPhoto(
        @Body photo :PhotoData
    )

    @GET("product")
    suspend fun getUserProducts(
        @Query("id_user") userId :Long,
        @Query("optionally") options :String = "user,property"
    ) :Array<FeedbackProduct>
}