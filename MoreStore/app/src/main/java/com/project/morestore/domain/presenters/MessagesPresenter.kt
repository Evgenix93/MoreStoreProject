package com.project.morestore.domain.presenters

import android.util.Log
import com.project.morestore.data.models.Chat
import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.ChatRepository
import com.project.morestore.presentation.mvpviews.MessagesMvpView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class MessagesPresenter @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
): MvpPresenter<MessagesMvpView>() {
    private var tabPosition: Int = 0

    fun handlePushMessageMessagesFragment(dialogId: Long, chatType: String){
        presenterScope.launch {
            val dialog = chatRepository.getDialogById(dialogId)?.body()
            val userId = authRepository.getUserId()
            dialog?.let {
                when(chatType){
                    Chat.Deal::class.simpleName -> {
                        if(it.product?.idUser != userId && it.dialog.user.id != 1L)
                            showDealDialogs(-1)
                    }
                    Chat.Lot::class.simpleName -> {
                        if(it.product?.idUser == userId)
                            showLotDialogs(-1)
                    }
                    else -> showAllDialogs(-1)
                }

            }

        }
    }

    fun showDealDialogs(userId: Long){
        presenterScope.launch {
            viewState.loading()
            tabPosition = 1
            val dialogs = getDealDialogs(userId)
            viewState.loaded(dialogs)
            (viewState as MessagesMvpView).showDialogCount(Chat.Deal::class.java.simpleName, dialogs.size)
            val isUnread = dialogs.find { it.isUnread } != null
            (viewState as MessagesMvpView).showUnreadTab(1, isUnread)
        }
    }

    fun showLotDialogs(userId: Long){
        presenterScope.launch {
            viewState.loading()
            tabPosition = 2
            val dialogs = getLotDialogs(userId)
            viewState.loaded(dialogs)
            (viewState as MessagesMvpView).showDialogCount(Chat.Lot::class.java.simpleName, dialogs.size)
            val isUnread = dialogs.find { it.isUnread } != null
            (viewState as MessagesMvpView).showUnreadTab(2, isUnread)

        }
    }

    fun showAllDialogs(userId: Long){
        presenterScope.launch {
            viewState.loading()
            tabPosition = 0
            val dealDialogs = getDealDialogs(userId)
            val lotDialogs = getLotDialogs(userId)
            val allDialogs = getSupportDialog() + dealDialogs + lotDialogs
            Log.d("MyTag", "viewState is MessagesMvpView: ${viewState is MessagesMvpView}")
            (viewState as MessagesMvpView).showDialogCount(Chat::class.java.simpleName, allDialogs.size)
            (viewState as MessagesMvpView).showDialogCount(Chat.Deal::class.java.simpleName, dealDialogs.size)
            (viewState as MessagesMvpView).showDialogCount(Chat.Lot::class.java.simpleName, lotDialogs.size)
            val isUnread = allDialogs.find { it.isUnread } != null
            val isUnreadDeals = dealDialogs.find { it.isUnread } != null
            val isUnreadLots = lotDialogs.find { it.isUnread } != null
            (viewState as MessagesMvpView).showUnreadTab(0, isUnread)
            (viewState as MessagesMvpView).showUnreadTab(1, isUnreadDeals)
            (viewState as MessagesMvpView).showUnreadTab(2, isUnreadLots)

            viewState.loaded(allDialogs)
        }
    }

    private suspend fun getDealDialogs(userId: Long): List<Chat>{
        viewState.loading()
        val currentUserId = authRepository.getUserId()
        val response = chatRepository.getDialogs()
        when (response?.code()) {
            200 -> {
                val chats = response.body()?.filter {it.product?.idUser != currentUserId && it.dialog.user.id != 1L}
                    ?.filter{
                        if(userId == -1L)
                            true
                        else
                            it.dialog.user.id == userId
                    }?.map { dialogWrapper ->
                        Chat.Deal(
                            dialogWrapper.dialog.id,
                            dialogWrapper.product?.name ?: "",
                            dialogWrapper.dialog.user.name.orEmpty(),
                            dialogWrapper.dialog.lastMessage?.idSender != currentUserId && dialogWrapper.dialog.lastMessage?.is_read == 0,
                            dialogWrapper.product?.photo?.first()?.photo ?: "",
                            dialogWrapper.product?.priceNew ?: 0f
                        )
                    }
                return chats.orEmpty()
            }
            else -> {
                viewState.error(errorMessage(response))
                return emptyList()
            }
        }
    }

    private suspend fun getLotDialogs(companionUserId: Long): List<Chat>{
        viewState.loading()
        val userId = authRepository.getUserId()
        val response = chatRepository.getDialogs()
        when (response?.code()) {
            200 -> {
                val products = response.body()?.filter{if(companionUserId == -1L)
                    true
                else
                    it.dialog.user.id == companionUserId}?.map { dilogWrapper ->
                    dilogWrapper.product

                }?.toSet()?.filter { it?.idUser == userId }
                val lots = products?.map { product ->
                    response.body()?.filter { dialogWrapper ->
                        dialogWrapper.product == product
                    }.orEmpty()
                }
                val chats =  lots?.map { lot ->  //response.body()?.filter {it.product.idUser == userId }?.map { dialogWrapper ->
                    val isUnread = lot.find { it.dialog.lastMessage?.idSender != userId && it.dialog.lastMessage?.is_read == 0 } != null
                    val buyersStr = when (lot.size){
                        1 -> "покупатель"
                        2,3,4 -> "покупателя"
                        else -> "покупателей"
                    }
                    Chat.Lot(
                        0,
                        lot.first().product?.name ?: "",
                        "${lot.size} $buyersStr",
                        isUnread,
                        lot.first().product?.photo?.first()?.photo ?: "",
                        lot.first().product?.priceNew ?: 0f,
                        lot.first().product?.id ?: 0

                    )

                }
                Log.d("mylog", chats?.size.toString())
                return chats.orEmpty()
            }
            else -> {
                viewState.error(errorMessage(response))
                return emptyList()
            }
        }
    }

    private suspend fun getSupportDialog(): List<Chat>{
        viewState.loading()
        val response = chatRepository.getDialogs()
        when (response?.code()) {
            200 -> {

                val chats = response.body()?.filter { dialogWrapper ->
                    dialogWrapper.dialog.user.id == 1L

                }?.map {
                    Chat.Support(
                        it.dialog.id,
                        "Служба поддержки",
                        it.dialog.lastMessage?.text ?: "Помощь с товаром"
                    )
                }
                return chats.orEmpty()
            }
            else -> {
                viewState.error(errorMessage(response))
                return emptyList()
            }
        }
    }

    fun getTabPosition(): Int = tabPosition

    fun showDialogs(userId: Long){
        when(tabPosition){
            0 -> showAllDialogs(userId)
            1 -> showDealDialogs(userId)
            2 -> showLotDialogs(userId)
        }
    }
}