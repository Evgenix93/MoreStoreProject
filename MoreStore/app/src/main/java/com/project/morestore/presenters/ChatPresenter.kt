package com.project.morestore.presenters

import android.util.Log
import com.project.morestore.mvpviews.ChatMvpView
import com.project.morestore.repositories.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class ChatPresenter: MvpPresenter<ChatMvpView>() {
    private val chatRepository = ChatRepository()

    fun createDialog(userId: Long, productId: Long){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.createDialog(userId, productId)
            when (response?.code()) {
                200 -> { getDialogById(response.body()?.id!!)}
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

            }

        }
    }

    fun getDialogById(id: Long){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.getDialogById(id)
            when (response?.code()) {
                200 -> { viewState.dialogLoaded(response.body()!!)}
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

            }

        }

    }

    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }


    }


}