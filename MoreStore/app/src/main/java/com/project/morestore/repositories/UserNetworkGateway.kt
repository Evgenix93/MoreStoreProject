package com.project.morestore.repositories

import com.project.morestore.apis.UserServerApi
import com.project.morestore.models.User
import com.project.morestore.singletones.Network

class UserNetworkGateway{
    private val network :UserServerApi = Network.userServerApi

    suspend fun getUser() :User? = network.getUserData().body()
}