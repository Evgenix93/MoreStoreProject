package com.project.morestore.domain.presenters

import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.ChatRepository
import com.project.morestore.presentation.mvpviews.BaseMvpView
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(private val authRepository: AuthRepository,
                            private val chatRepository: ChatRepository): MvpPresenter<BaseMvpView>() {
    fun showUnreadMessages(){
        presenterScope.launch {
            val userId = authRepository.getUserId()
            val response = chatRepository.getDialogs()
            if(response?.code() != 200)
                return@launch
            val dialogs = response.body()
            dialogs ?: return@launch
            val unreadDialog = dialogs.find { it.dialog.lastMessage?.is_read == 0 && it.dialog.lastMessage.idSender != userId }
            viewState.loaded(unreadDialog != null)
        }
    }

}