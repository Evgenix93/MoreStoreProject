package com.project.morestore.presenters

import android.content.Context
import android.util.Log
import com.project.morestore.mvpviews.ChatMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class ChatPresenter(context: Context) : MvpPresenter<ChatMvpView>() {
    private val chatRepository = ChatRepository()
    private val authRepository = AuthRepository(context)
    private var dialogId: Long? = null

    fun createDialog(userId: Long, productId: Long) {
        presenterScope.launch {
            viewState.currentUserIdLoaded(authRepository.getUserId())
            viewState.loading()
            val response = chatRepository.createDialog(userId, productId)
            when (response?.code()) {
                200 -> {

                    getDialogById(response.body()?.id!!)
                }
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

    fun getDialogById(id: Long) {
        presenterScope.launch {
            dialogId = id
            viewState.currentUserIdLoaded(authRepository.getUserId())
            viewState.loading()
            val response = chatRepository.getDialogById(id)
            when (response?.code()) {
                200 -> {
                    viewState.dialogLoaded(response.body()!!)
                }
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

    fun addMessage(text: String){
        presenterScope.launch {
            val response = chatRepository.addMessage(text, dialogId ?: 0)
            when (response?.code()) {
                200 -> {
                    viewState.messageSent(response.body()!!)
                }
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

    fun getDialogs(){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.getDialogs()
            when (response?.code()) {
                200 -> {
                    viewState.dialogsLoaded(response.body()!!)
                }
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