package com.project.morestore.mvpviews

import android.net.Uri
import com.project.morestore.models.*
import com.project.morestore.util.MessageActionType
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface ChatMvpView: MvpView {

    @OneExecution
    fun loading()

    @OneExecution
    fun dialogsLoaded(dialogs: List<Chat>)

    @OneExecution
    fun dialogLoaded(dialog: DialogWrapper)

    @OneExecution
    fun dialogCreated(dialogId: CreatedDialogId)

    @OneExecution
    fun error(message: String)

    @OneExecution
    fun currentUserIdLoaded(id: Long)

    @OneExecution
    fun messageSent(message: MessageModel)

    @OneExecution
    fun dialogDeleted()

    @OneExecution
    fun photoVideoLoaded()

    @OneExecution
    fun showDialogCount(type: String, count: Int)

    @OneExecution
    fun mediaUrisLoaded(mediaUris: List<Uri>?)

    @OneExecution
    fun actionMessageSent(info: ChatFunctionInfo, type: MessageActionType )

    //@OneExecution
    //fun productInfoLoaded(product: Product)


}