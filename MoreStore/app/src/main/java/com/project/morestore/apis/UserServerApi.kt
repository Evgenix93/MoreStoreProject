package com.project.morestore.apis

import com.project.morestore.models.User
import retrofit2.Response
import retrofit2.http.GET

interface UserServerApi {//todo rename to "UserApi"
    @GET("user/islogin")
    suspend fun getUserData(): Response<User>
}