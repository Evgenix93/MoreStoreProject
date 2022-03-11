package com.project.morestore.mvpviews

import com.project.morestore.models.Chat
import com.project.morestore.models.CreatedDialogId
import com.project.morestore.models.DialogWrapper
import com.project.morestore.models.MessageModel
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface ChatMvpView: MvpView {

    @OneExecution
    fun loading()

    @OneExecution
    fun dialogsLoaded(dialogs: List<DialogWrapper>)

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


}