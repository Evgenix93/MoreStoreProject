package com.project.morestore.presenters

import android.content.Context
import android.net.Uri
import android.util.Log
import com.project.morestore.models.Chat
import com.project.morestore.models.ChatFunctionInfo
import com.project.morestore.mvpviews.ChatMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ChatRepository
import com.project.morestore.util.MessageActionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class ChatPresenter(context: Context) : MvpPresenter<ChatMvpView>() {
    private val chatRepository = ChatRepository(context)
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

    private suspend fun getSupportDialog(): List<Chat>{
       // presenterScope.launch {
            viewState.loading()
           // val userId = authRepository.getUserId()
            val response = chatRepository.getDialogs()
            when (response?.code()) {
                200 -> {
                    //val chats = response.body()?.map { dialogWrapper ->
                       // if(dialogWrapper.product.idUser == userId )
                            //Chat.Lot()
                    //}
                    //viewState.dialogsLoaded(response.body()!!)
                    val chats = response.body()?.filter { dialogWrapper ->
                        dialogWrapper.dialog.user.id == 1L

                    }?.map {
                        Chat.Support(
                            it.dialog.id,
                            "Служба поддержки",
                            "Помощь с товаром"
                        )
                    }
                    return chats.orEmpty()
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                    return emptyList()
                }
                500 -> {
                    viewState.error("500 Internal Server Error")
                    return emptyList()
                }
                null -> {
                    viewState.error("нет интернета")
                    return emptyList()
                }
                else -> {
                    viewState.error("ошибка")
                    return emptyList()
                }

            }


        //}
    }

    fun deleteDialog(dialogId: Long){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.deleteDialog(dialogId)
            when (response?.code()) {
                200 -> {
                    viewState.dialogDeleted()
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

    private suspend fun getDealDialogs(): List<Chat>{
       // presenterScope.launch {
            viewState.loading()
            val userId = authRepository.getUserId()
            val response = chatRepository.getDialogs()
            when (response?.code()) {
                200 -> {
                    val chats = response.body()?.filter {it.product?.idUser != userId && it.dialog.user.id != 1L }?.map { dialogWrapper ->
                            Chat.Deal(
                                dialogWrapper.dialog.id,
                                dialogWrapper.product?.name ?: "",
                                dialogWrapper.dialog.user.name.orEmpty(),
                                dialogWrapper.product?.photo?.first()?.photo ?: "",
                                dialogWrapper.product?.priceNew ?: 0f

                            )

                    }
                    //viewState.dialogsLoaded(chats.orEmpty())
                    return chats.orEmpty()
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                    return emptyList()

                }
                500 -> {
                    viewState.error("500 Internal Server Error")
                return emptyList()}
                null -> {
                    viewState.error("нет интернета")
                return emptyList()}
                else -> {
                    viewState.error("ошибка")
                    return emptyList()
                }

            }


       // }

    }

    private suspend fun getLotDialogs(): List<Chat>{
       // presenterScope.launch {
            viewState.loading()
            val userId = authRepository.getUserId()
            val response = chatRepository.getDialogs()
            when (response?.code()) {
                200 -> {
                    val products = response.body()?.map { dilogWrapper ->
                        dilogWrapper.product

                    }?.toSet()?.filter { it?.idUser == userId }
                    val lots = products?.map { product ->
                        response.body()?.filter { dialogWrapper ->
                            dialogWrapper.product == product
                        }.orEmpty()


                    }
                    val chats =  lots?.map { lot ->  //response.body()?.filter {it.product.idUser == userId }?.map { dialogWrapper ->
                        Chat.Lot(
                            0,
                            lot.first().product?.name ?: "",
                            "${lot.size.toString()} покупателей",
                            lot.first().product?.photo?.first()?.photo ?: "",
                            lot.first().product?.priceNew ?: 0f,
                            lot.first().product?.id ?: 0

                        )

                    }
                    Log.d("mylog", chats?.size.toString())
                    return chats.orEmpty()
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                    return emptyList()
                }
                500 -> {
                    viewState.error("500 Internal Server Error")
                    return emptyList()
                }
                null -> {
                    viewState.error("нет интернета")
                    return emptyList()
                }
                else -> {
                    viewState.error("ошибка")
                    return emptyList()
                }

            }


        //}
    }

    private suspend fun getDialogsByProductId(id: Long): List<Chat>{
        val response = chatRepository.getDialogs()
        when (response?.code()) {
            200 -> {
                val chats = response.body()?.filter { dialogWrapper ->
                    dialogWrapper.product?.id == id

                }?.map {
                    Chat.Personal(
                        it.dialog.id,
                        it.dialog.user.name.orEmpty(),
                        it.dialog.lastMessage?.text.orEmpty(),
                        it.dialog.user.avatar?.photo.toString(),
                        it.product?.priceNew ?: 0f


                    )

                }
                return chats.orEmpty()
            }
            400 -> {
                val bodyString = getStringFromResponse(response.errorBody()!!)
                viewState.error(bodyString)
                return emptyList()
            }
            500 -> {
                viewState.error("500 Internal Server Error")
                return emptyList()
            }
            null -> {
                viewState.error("нет интернета")
                return emptyList()
            }
            else -> {
                viewState.error("ошибка")
                return emptyList()
            }

        }


    }

    fun showDealDialogs(){
        presenterScope.launch {
            viewState.dialogsLoaded(getDealDialogs())
        }
    }

    fun showLotDialogs(){
        presenterScope.launch {
            viewState.dialogsLoaded(getLotDialogs())
        }
    }

    fun showAllDialogs(){
        presenterScope.launch {
            val dealDialogs = getDealDialogs()
            val lotDialogs = getLotDialogs()
            val allDialogs = getSupportDialog() + dealDialogs + lotDialogs
            viewState.showDialogCount(Chat::class.java.simpleName, allDialogs.size)
            viewState.showDialogCount(Chat.Deal::class.java.simpleName, dealDialogs.size)
            viewState.showDialogCount(Chat.Lot::class.java.simpleName, lotDialogs.size)

            viewState.dialogsLoaded(allDialogs)
        }
    }

    fun showProductDialogs(id: Long){
        presenterScope.launch {
            viewState.dialogsLoaded(getDialogsByProductId(id))
        }
    }

    fun uploadPhotoVideo(uris: List<Uri>, message: String){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.addMessage(message, dialogId ?: 0)
            when (response?.code()) {
                200 -> {
                   val photoLoaded = uploadPhotos(uris, response.body()!!.id)
                   val videoLoaded = uploadVideos(uris, response.body()!!.id)
                    if(photoLoaded || videoLoaded)
                        viewState.photoVideoLoaded()
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

    private suspend fun uploadPhotos(uris: List<Uri>, messageId: Long): Boolean{
        val response = chatRepository.uploadPhotos(uris, messageId)
        when (response?.code()) {
            200 -> {
                return true
            }
            400 -> {
                val bodyString = getStringFromResponse(response.errorBody()!!)
                viewState.error(bodyString)
                return false
            }
            500 -> {
                viewState.error("500 Internal Server Error")
                return false
            }
            null -> {
              //  viewState.error("нет интернета")
                return false
            }
            else -> {
                viewState.error("ошибка")
                return false
            }

        }
    }

    private suspend fun uploadVideos(uris: List<Uri>, messageId: Long): Boolean{
        val response = chatRepository.uploadVideos(uris, messageId)
        when (response?.code()) {
            200 -> {
                return true
            }
            400 -> {
                val bodyString = getStringFromResponse(response.errorBody()!!)
                viewState.error(bodyString)
                return false
            }
            500 -> {
                viewState.error("500 Internal Server Error")
                return false
            }
            null -> {
               // viewState.error("нет интернета")
                return false
            }
            else -> {
                viewState.error("ошибка")
                return false
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

    fun loadMediaUris() {
        viewState.mediaUrisLoaded(chatRepository.loadMediaUris())
    }

    fun clearMediaUris(){
        chatRepository.clearMediaUris()
    }

    fun offerDiscount(info: ChatFunctionInfo){
        viewState.loading()
        presenterScope.launch {
            val response = chatRepository.offerDiscount(info)
            when(response?.code()){
                200 -> {
                    submitDiscount(ChatFunctionInfo(idDialog = response.body()!!.idDialog, suggest = response.body()!!.idSuggest))
                }
                null -> viewState.error("Ошибка")
            }
        }
    }

    private fun submitDiscount(info: ChatFunctionInfo){
        presenterScope.launch {
            val response = chatRepository.submitDiscount(info)
            when(response?.code()){
                200 -> {
                    viewState.actionMessageSent(response.body()!!, MessageActionType.DiscountRequestSuggest)
                }
                null -> viewState.error("Ошибка")
            }
        }
    }

    fun submitBuy(info: ChatFunctionInfo){
        presenterScope.launch {
         val response = chatRepository.submitBuy(info)
           when(response?.code()){
               200 -> viewState.actionMessageSent(response.body()!!, MessageActionType.BuyRequestSubmit)
               null -> viewState.error("Ошибка")
           }
        }
    }

    fun submitPrice(info: ChatFunctionInfo){
        presenterScope.launch {
            val response = chatRepository.submitPrice(info)
            when(response?.code()){
                200 -> viewState.actionMessageSent(response.body()!!, MessageActionType.PriceRequestSubmit)
                null -> viewState.error("Ошибка")
            }
        }
    }

    fun cancelPrice(info: ChatFunctionInfo){
        presenterScope.launch {
            val response = chatRepository.cancelPrice(info)
            when(response?.code()){
                200 -> viewState.actionMessageSent(response.body()!!, MessageActionType.PriceRequestCancel)
                null -> viewState.error("Ошибка")
            }
        }
    }
}