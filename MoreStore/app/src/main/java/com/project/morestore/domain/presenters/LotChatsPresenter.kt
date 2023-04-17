package com.project.morestore.domain.presenters

import com.project.morestore.data.models.Chat
import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.ChatRepository
import com.project.morestore.presentation.mvpviews.ResultLoadedMvpView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class LotChatsPresenter @Inject constructor(private val chatRepository: ChatRepository,
                        private val authRepository: AuthRepository
): MvpPresenter<ResultLoadedMvpView>() {

    fun handlePushMessageLotsChatFragment(dialogId: Long, productId: Long){
        presenterScope.launch {
            val dialog = chatRepository.getDialogById(dialogId)?.body()
            dialog?.let {
                if(it.product?.id == productId)
                    showProductDialogs(productId)
            }
        }
    }

    fun showProductDialogs(id: Long){
        presenterScope.launch {
            viewState.loaded(getDialogsByProductId(id))
        }
    }

    private suspend fun getDialogsByProductId(id: Long): List<Chat>{
        val response = chatRepository.getDialogs()
        when (response?.code()) {
            200 -> {
                val userId = authRepository.getUserId()
                val chats = response.body()?.filter { dialogWrapper ->
                    dialogWrapper.product?.id == id

                }?.map {
                    Chat.Personal(
                        it.dialog.id,
                        it.dialog.user.name.orEmpty(),
                        it.dialog.lastMessage?.text.orEmpty(),
                        it.dialog.lastMessage?.idSender != userId && it.dialog.lastMessage?.is_read == 0,
                        it.dialog.user.avatar?.photo.toString(),
                        it.product?.priceNew ?: 0f
                    )
                }
                return chats.orEmpty()
            }
            else -> {
                return emptyList()
            }
        }
    }
}