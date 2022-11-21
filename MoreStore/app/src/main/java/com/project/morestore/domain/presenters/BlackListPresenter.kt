package com.project.morestore.domain.presenters

import com.project.morestore.presentation.mvpviews.BlackListMvpView
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
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