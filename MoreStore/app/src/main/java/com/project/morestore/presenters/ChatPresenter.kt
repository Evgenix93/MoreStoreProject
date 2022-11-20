package com.project.morestore.presenters

import android.content.Context
import android.net.Uri
import android.util.Log
import com.project.morestore.models.Chat
import com.project.morestore.models.ChatFunctionInfo
import com.project.morestore.models.Id
import com.project.morestore.models.User
import com.project.morestore.models.cart.CartItem
import com.project.morestore.mvpviews.ChatMvpView
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.mvpviews.MessagesMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ChatRepository
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.UserRepository
import com.project.morestore.util.MessageActionType
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody
import javax.inject.Inject

class ChatPresenter @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
     private val ordersRepository: OrdersRepository,
    private val userRepository: UserRepository) : MvpPresenter<MainMvpView>() {

    private var dialogId: Long? = null
    private var tabPosition: Int = 0

    fun createDialog(userId: Long, productId: Long){
        presenterScope.launch {
            (viewState as ChatMvpView).currentUserIdLoaded(authRepository.getUserId())
            viewState.loading()
            val response = chatRepository.createDialog(userId, productId)
            when (response?.code()) {
                200 -> {
                    getDialogById(response.body()?.id!!)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getDialogById(id: Long) {
        presenterScope.launch {
            dialogId = id
            (viewState as ChatMvpView).currentUserIdLoaded(authRepository.getUserId())
            viewState.loading()
            val response = chatRepository.getDialogById(id)
            when (response?.code()) {
                200 -> {
                    val currentUserId = authRepository.getUserId()
                    val product = response.body()?.product
                    val status = when(product?.statusUser?.order?.status) {
                        0 -> if (product.statusUser.order.idUser == currentUserId && product.statusUser.buy?.status != 2) 6
                        else if(product.idUser == currentUserId && product.statusUser.buy?.status != 2) 6
                        else if(product.statusUser.buy?.status != 2) 7 else 1
                        1 -> 8
                        else -> 1
                    }
                    response.body()?.product?.status = status
                    (viewState as ChatMvpView).dialogLoaded(response.body()!!)
                }
                else -> viewState.error(errorMessage(response))
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

    fun addMessage(text: String){
        presenterScope.launch {
            val response = chatRepository.addMessage(text, dialogId ?: 0)
            when (response?.code()) {
                200 -> {
                    (viewState as ChatMvpView).messageSent(response.body()!!)
                }
                else -> viewState.error(errorMessage(response))
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

    fun deleteDialog(dialogId: Long){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.deleteDialog(dialogId)
            when (response?.code()) {
                200 -> {
                    (viewState as ChatMvpView).dialogDeleted()
                }
                else -> viewState.error(errorMessage(response))
            }
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
                            2 -> "покупателя"
                            3 -> "покупателя"
                            4 -> "покупателя"
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
                viewState.error(errorMessage(response))
                return emptyList()
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

    fun showProductDialogs(id: Long){
        presenterScope.launch {
            viewState.loading()
            viewState.loaded(getDialogsByProductId(id))
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
                        (viewState as ChatMvpView).photoVideoLoaded()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    private suspend fun uploadPhotos(uris: List<Uri>, messageId: Long): Boolean{
        val response = chatRepository.uploadPhotos(uris, messageId)
        return when (response?.code()) {
            200 -> {
                true
            }
            else -> {
                viewState.error(errorMessage(response))
                false
            }
        }
    }

    private suspend fun uploadVideos(uris: List<Uri>, messageId: Long): Boolean{
        val response = chatRepository.uploadVideos(uris, messageId)
        return when (response?.code()) {
            200 -> {
                true
            }
            else -> {
                viewState.error(errorMessage(response))
                false
            }
        }
    }

    fun sendBuyRequest(dialogId: Long){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.sendBuyRequest(ChatFunctionInfo(dialogId = dialogId))
            when (response?.code()) {
                200 -> {
                    (viewState as ChatMvpView).actionMessageSent(response.body()!!, MessageActionType.BUY_REQUEST_SUGGEST)
                }
                else -> {
                    viewState.error(errorMessage(response))
                }
            }
        }
    }

    private suspend fun sendSuspendBuyRequest(dialogId: Long){
        val response = chatRepository.sendBuyRequest(ChatFunctionInfo(dialogId = dialogId))
            when (response?.code()) {
                200 -> {
                    (viewState as ChatMvpView).actionMessageSent(response.body()!!, MessageActionType.BUY_REQUEST_SUGGEST)
                }
                else -> {
                    viewState.error(errorMessage(response))
                }
            }


    }

    fun cancelBuyRequest(dialogId: Long, suggestId: Long){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.cancelBuyRequest(ChatFunctionInfo(dialogId = dialogId, suggest = suggestId))
            when (response?.code()) {
                200 -> {
                    (viewState as ChatMvpView).actionMessageSent(response.body()!!, MessageActionType.BUY_REQUEST_CANCEL)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun sendPriceSuggest(dialogId: Long, value: Int){
        presenterScope.launch {
            viewState.loading()
            val response = chatRepository.sendPriceSuggest(ChatFunctionInfo(dialogId = dialogId, value = value))
            when (response?.code()) {
                200 -> {
                    (viewState as ChatMvpView).actionMessageSent(response.body()!!, MessageActionType.PRICE_SUGGEST)
                }
                else -> viewState.error(errorMessage(response))
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
        (viewState as ChatMvpView).mediaUrisLoaded(chatRepository.loadMediaUris())
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
                    submitDiscount(ChatFunctionInfo(dialogId = response.body()!!.dialogId, suggest = response.body()!!.suggestId))
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    private fun submitDiscount(info: ChatFunctionInfo){
        presenterScope.launch {
            val response = chatRepository.submitDiscount(info)
            when(response?.code()){
                200 -> {
                    (viewState as ChatMvpView).actionMessageSent(response.body()!!, MessageActionType.DISCOUNT_REQUEST_SUBMIT)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun submitBuy(info: ChatFunctionInfo){
        presenterScope.launch {
         val response = chatRepository.submitBuy(info)
           when(response?.code()){
               200 -> (viewState as ChatMvpView).actionMessageSent(response.body()!!, MessageActionType.BUY_REQUEST_SUBMIT)
               else -> viewState.error(errorMessage(response))
           }
        }
    }

    fun submitPrice(info: ChatFunctionInfo){
        presenterScope.launch {
            val response = chatRepository.submitPrice(info)
            when(response?.code()){
                200 -> (viewState as ChatMvpView).actionMessageSent(response.body()!!, MessageActionType.PRICE_REQUEST_SUBMIT)
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun cancelPrice(info: ChatFunctionInfo){
        presenterScope.launch {
            val response = chatRepository.cancelPrice(info)
            when(response?.code()){
                200 -> (viewState as ChatMvpView).actionMessageSent(response.body()!!, MessageActionType.PRICE_REQUEST_CANCEL)
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun readMessages(dialogId: Long){
        presenterScope.launch {
            val response = chatRepository.readMessages(Id(dialogId))
            when (response?.code()) {
                200 -> {
                    showUnreadMessages()

                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun handlePushMessageLotsChatFragment(dialogId: Long, productId: Long){
        presenterScope.launch {
            val dialog = chatRepository.getDialogById(dialogId)?.body()
            dialog?.let {
                if(it.product?.id == productId)
                    showProductDialogs(productId)
            }

        }
    }

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

    fun buyProduct(productId: Long, userId: Long){
        presenterScope.launch {
            viewState.loading()
            val productAdded = addProductToCart(productId, userId)
            if(productAdded.not()) return@launch
            val cartItem = loadCartData(userId)?.find { it.product.id == productId }
            cartItem?.let { (viewState as ChatMvpView).productAddedToCart(it.product, it.id) }
        }

    }

    private suspend fun showUnreadMessages() {
        val userId = authRepository.getUserId()
        val response = chatRepository.getDialogs()
        if (response?.code() != 200)
            return
        val dialogs = response.body()
        dialogs ?: return
        val unreadDialog =
            dialogs.find { it.dialog.lastMessage?.is_read == 0 && it.dialog.lastMessage.idSender != userId }
        (viewState as ChatMvpView).showUnreadMessagesStatus(unreadDialog != null)
    }

    private suspend fun loadCartData(
        userId: Long? = null
    ): List<CartItem>? {
            val response = ordersRepository.getCartItems(
                userId = userId
            )

            return when (response?.code()) {
                200 -> {
                    response.body()
                }

                404 -> {
                    viewState.error("товар не был добавлен в корзину")
                    null
                }
                else -> {
                    viewState.error(errorMessage(response))
                    null
                }
            }
    }

    private  suspend fun addProductToCart(
        productId: Long,
        userId: Long? = null,
    ): Boolean {
        val response = ordersRepository.addCartItem(
                productId = productId,
                userId = userId
            )

            return when (response?.code()) {
                200 -> {
                    true
                }
                else -> {
                    viewState.error(errorMessage(response))
                    false
                }
            }
    }

    fun blockUser(userId: Long){
        presenterScope.launch {
            viewState.loading()
            val blockedUsers = getBlockedUsers()
            blockedUsers ?: return@launch
            val isUserBlocked = blockedUsers.find { it.id == userId } != null
            if(isUserBlocked){
                viewState.error("Пользователь уже заблокирован")
            }else{
                val success = blockUnblockUser(userId)
                if(success == true) viewState.error("Пользователь заблокирован")
            }
        }
    }

    private suspend fun getBlockedUsers(): List<User>? {
        val response = userRepository.getBlackList()
        return when (response?.code()) {
            200 -> {
                response.body()
            }

            404 -> {

                emptyList()
            }
            else -> {
                viewState.error(errorMessage(response))
                null
            }
        }
    }

    private suspend fun blockUnblockUser(id: Long): Boolean? {
        val response = userRepository.blockUnblockUser(id)
        return when (response?.code()) {
            200 -> {
                response.body() == "block" || response.body() == "unblock"
            }

            404 -> {
                null
            }
            else -> {
                viewState.error(errorMessage(response))
                null
            }
        }
    }
}