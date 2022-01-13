package com.project.morestore.apis

import com.project.morestore.models.PhotoData
import com.project.morestore.models.RegistrationResponse
import com.project.morestore.models.RegistrationResponse2
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    @POST("user/put")
    suspend fun changeUserData(
        @Query("email") email: String?,
        @Query("phone") phone: String?,
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("sex") sex: String?,
        @Query("step") step: Int?,
        @Query("code") code: Int?
    ): Response<RegistrationResponse>

    @POST("user/put")
    suspend fun changeUserDataGetError(
        @Query("email") email: String?,
        @Query("phone") phone: String?,
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("sex") sex: String?,
        @Query("step") step: Int?,
        @Query("code") code: Int?
    ): Response<String>



    @POST("user/put")
    suspend fun changeUserData2(
        @Query("email") email: String?,
        @Query("phone") phone: String?,
        @Query("step") step: Int?,
        @Query("code") code: Int?
    ): Response<RegistrationResponse2>

    @POST("user/put")
    suspend fun changeUserData2GetError(
        @Query("email") email: String?,
        @Query("phone") phone: String?,
        @Query("step") step: Int?,
        @Query("code") code: Int?
    ): Response<String>

    @POST("upload/photo")
    suspend fun uploadPhoto(@Body photoData: PhotoData): Response<Unit>

    @POST("upload/photo")
    suspend fun uploadPhotoGetError(@Body photoData: PhotoData): Response<String>


}