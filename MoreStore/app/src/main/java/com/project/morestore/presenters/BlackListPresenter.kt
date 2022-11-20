package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.mvpviews.BlackListMvpView
import com.project.morestore.repositories.UserRepository
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody
import javax.inject.Inject

class BlackListPresenter @Inject constructor(private val userRepository: UserRepository): MvpPresenter<BlackListMvpView>() {

    fun getBlackList(){
        viewState.loading(true)
        presenterScope.launch {
            val response = userRepository.getBlackList()
            when(response?.code()){
                200 -> {
                    viewState.loading(false)
                    viewState.onEmptyList(response.body()!!.isEmpty())
                    viewState.onBlackListLoaded(response.body()!!)
                }
                404 -> {
                    viewState.loading(false)
                    viewState.onEmptyList(true)
                }
                else -> {
                    viewState.onEmptyList(true)
                    viewState.loading(false)
                    viewState.onError(errorMessage(response))}
            }
        }
    }

    fun blockUnblockUser(id: Long){
        presenterScope.launch {
            viewState.loading(true)
            val response = userRepository.blockUnblockUser(id)
            when(response?.code()){
                200 -> {
                    viewState.loading(false)
                    if(response.body() == "unblock")
                        viewState.onBlockUnblockUser()
                    else viewState.onError(response.body()!!)
                }
                else -> {
                    viewState.loading(false)
                    viewState.onError(errorMessage(response))
                }
            }
        }
    }
}