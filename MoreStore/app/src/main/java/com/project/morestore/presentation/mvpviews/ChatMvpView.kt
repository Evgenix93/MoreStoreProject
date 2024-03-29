package com.project.morestore.presentation.mvpviews

import android.net.Uri
import com.project.morestore.data.models.*
import com.project.morestore.util.MessageActionType
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface ChatMvpView: LoadingMvpView {

    @OneExecution
    fun dialogLoaded(dialog: DialogWrapper)


    @OneExecution
    fun currentUserIdLoaded(id: Long)

    @OneExecution
    fun messageSent(message: MessageModel)

    @OneExecution
    fun dialogDeleted()

    @OneExecution
    fun photoVideoLoaded()

    @OneExecution
    fun mediaUrisLoaded(mediaUris: List<Uri>?)

    @OneExecution
    fun actionMessageSent(info: ChatFunctionInfo, type: MessageActionType )

    @OneExecution
    fun showUnreadMessagesStatus(show: Boolean)

    @OneExecution
    fun productAddedToCart(product: Product, cartId: Long)

}