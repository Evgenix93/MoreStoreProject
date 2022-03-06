package com.project.morestore.mvpviews

import com.project.morestore.models.Chat
import com.project.morestore.models.CreatedDialogId
import com.project.morestore.models.DialogWrapper
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


}