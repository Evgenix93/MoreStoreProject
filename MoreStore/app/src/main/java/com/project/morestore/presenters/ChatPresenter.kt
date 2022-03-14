package com.project.morestore.presenters

import android.content.Context
import android.net.Uri
import android.util.Log
import com.project.morestore.models.Chat
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
                    val chats = response.body()?.filter {it.product.idUser != userId }?.map { dialogWrapper ->
                            Chat.Deal(
                                dialogWrapper.dialog.id,
                                dialogWrapper.product.name,
                                dialogWrapper.dialog.user.name.orEmpty(),
                                dialogWrapper.product.photo.first().photo,
                                dialogWrapper.product.priceNew ?: 0f

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

                    }?.toSet()?.filter { it.idUser == userId }
                    val lots = products?.map { product ->
                        response.body()?.filter { dialogWrapper ->
                            dialogWrapper.product == product
                        }.orEmpty()


                    }
                    val chats =  lots?.map { lot ->  //response.body()?.filter {it.product.idUser == userId }?.map { dialogWrapper ->
                        Chat.Lot(
                            0,
                            lot.first().product.name,
                            "${lot.size.toString()} покупателей",
                            lot.first().product.photo.first().photo,
                            lot.first().product.priceNew ?: 0f,
                            lot.first().product.id

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
                    dialogWrapper.product.id == id

                }?.map {
                    Chat.Personal(
                        it.dialog.id,
                        it.dialog.user.name.orEmpty(),
                        it.dialog.lastMessage?.text.orEmpty(),
                        it.dialog.user.avatar?.photo.orEmpty(),
                        it.product.priceNew ?: 0f


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
            viewState.dialogsLoaded(getSupportDialog() + getDealDialogs() + getLotDialogs())
        }
    }

    fun showProductDialogs(id: Long){
        presenterScope.launch {
            viewState.dialogsLoaded(getDialogsByProductId(id))
        }
    }

    fun uploadPhotoVideo(uris: List<Uri>){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.addMessage("", dialogId ?: 0)
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


}