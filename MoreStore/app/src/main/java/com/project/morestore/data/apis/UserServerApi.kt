package com.project.morestore.data.apis

import com.project.morestore.data.models.User
import retrofit2.Response
import retrofit2.http.GET

interface UserServerApi {//todo rename to "UserApi"
    @GET("user/islogin")
    suspend fun getUserData(): Response<User>
}