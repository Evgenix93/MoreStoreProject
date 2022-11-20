package com.project.morestore.repositories

import com.project.morestore.apis.UserServerApi
import com.project.morestore.data.models.User
import javax.inject.Inject

class UserNetworkGateway @Inject constructor(private val network :UserServerApi){


    suspend fun getUser() :User? = network.getUserData().body()
}