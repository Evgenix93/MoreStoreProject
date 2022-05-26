package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.mvpviews.BlackListMvpView
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class BlackListPresenter(context: Context): MvpPresenter<BlackListMvpView>() {
    private val userRepository = UserRepository(context)

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
                0 -> {
                    viewState.onEmptyList(true)
                    viewState.loading(false)
                    viewState.onError(getErrorString(response.errorBody()!!))}
            }
        }
    }

    fun blockUnblockUser(){
        viewState.onBlockUnblockUser()
    }

    private suspend fun getErrorString(body: ResponseBody): String{
        return withContext(Dispatchers.IO){
            body.string()
        }
    }
}