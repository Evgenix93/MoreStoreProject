package com.project.morestore.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.MessagesAdapter
import com.project.morestore.databinding.FragmentChatBinding
import com.project.morestore.databinding.WidgetBuyBarBinding
import com.project.morestore.databinding.WidgetDealCancelBinding
import com.project.morestore.databinding.WidgetSellBarBinding
import com.project.morestore.dialogs.PriceDialog
import com.project.morestore.dialogs.MenuBottomDialogFragment
import com.project.morestore.fragments.base.FullscreenMvpFragment
import com.project.morestore.models.*
import com.project.morestore.mvpviews.ChatMvpView
import com.project.morestore.presenters.ChatPresenter
import com.project.morestore.util.MessageActionType
import com.project.morestore.util.MessagingService
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace
import dev.jorik.stub.defToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.ktx.moxyPresenter
import java.util.*

class ChatFragment : FullscreenMvpFragment(), MenuBottomDialogFragment.Callback,
    PriceDialog.Callback, ChatMvpView {
    private lateinit var views: FragmentChatBinding
    private lateinit var cancelWidgetBinding: WidgetDealCancelBinding
    private val presenter by moxyPresenter { ChatPresenter(requireContext()) }
    private var currentUserId: Long? = null
    private var currentDialogId: Long? = null
    private var currentProductPrice: Float? = null
    private var currentProductId: Long? = null
    private lateinit var filePicker: ActivityResultLauncher<Array<String>>
    private val adapter = MessagesAdapter(
        acceptDealCallback = {
              presenter.submitBuy(ChatFunctionInfo(
                   dialogId = requireArguments().getLong(DIALOG_ID_KEY),
                   suggest = (it as Message.Special.DealRequest).suggestId,
                   value = it.price
              ))
           // stubAcceptDealRunnable.run()
           // views.bottomBar.visibility = GONE
        },
       cancelDealCallback =  {  presenter.cancelBuyRequest(currentDialogId ?: 0, (it as Message.Special.DealRequest).suggestId ) },
       submitPriceCallback =  {
           presenter.submitPrice(ChatFunctionInfo(
               dialogId = requireArguments().getLong(DIALOG_ID_KEY),
               suggest = (it as Message.Special.PriceSubmit).suggestId
           ))
        }, cancelPriceCallback = {
           presenter.cancelPrice(ChatFunctionInfo(
               dialogId = requireArguments().getLong(DIALOG_ID_KEY),
               suggest = (it as Message.Special.PriceSubmit).suggestId
           ))
        },
       showMediaCallback =  { media ->
            val uris =
                media.map { if (it is Media.Photo) it.photoUri else (it as Media.Video).videoUri }
            findNavController().navigate(
                R.id.action_chatFragment_to_mediaFragment,
                bundleOf(MediaFragment.PHOTOS to uris.toTypedArray())
            )
        },
        onDealDetailsCallback = {
            if(it != -1L)
            findNavController().navigate(ChatFragmentDirections.actionChatFragmentToOrderDetailsFragment(orderId = it))
        }
    )
    private var listenGeo = false
    private lateinit var user: User
    private var mediaUris: List<Uri>? = null
    private val messageBroadCastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            val dialogId = intent?.getLongExtra(MessagingService.MESSAGE_KEY, -1L)
            dialogId?.let {
                Log.d("fire", "current $currentDialogId, id $it")
                if(currentDialogId == it) {
                    lifecycleScope.launch {
                        delay(2000)
                        getDialog(it)
                    }
                }
            }

        }

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChatBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerReceiver()
        setClickListeners()
        with(views) {
            toolbar.toolbar.setNavigationOnClickListener {
                if(findNavController().previousBackStackEntry?.destination?.id ==
                    R.id.createOrderFragment) findNavController().navigate(R.id.messagesFragment)
                else findNavController().popBackStack() }
            list.setSpace(24.dp)
            list.adapter = adapter
        }
        when (val chatType = requireArguments().getString(Chat::class.java.simpleName)) {
            Chat.Support::class.java.simpleName -> {
                val dialogId = requireArguments().getLong(DIALOG_ID_KEY, 0)
                getDialog(dialogId)
            }
            Chat.Personal::class.java.simpleName -> {
                val dialogId = requireArguments().getLong(DIALOG_ID_KEY, 0)
                getDialog(dialogId)


            }
            Chat.Deal::class.java.simpleName -> {
                val userId = requireArguments().getLong(USER_ID_KEY, 0)
                val productId = requireArguments().getLong(PRODUCT_ID_KEY, 0)
                val dialogId = requireArguments().getLong(DIALOG_ID_KEY, 0)
                if (userId != 0L && productId != 0L)
                    createDealChat(userId, productId, findNavController().previousBackStackEntry?.destination?.id == R.id.createOrderFragment)
                else getDialog(dialogId)
            }
            else -> throw IllegalArgumentException("Undefined chat type: $chatType")
        }

        initFilePicker()
        loadMediaUris()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.clearMediaUris()
        mediaUris = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterReceiver()
    }

    /* private fun showSell(){
        // adapter.avatarUri = R.drawable.user2
         with(views){
             toolbar.title.text = "?????????? ??."
             toolbar.subtitle.text = "?? ???????? 2 ??. ??????????"
             toolbar.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_dropdown, 0)
             toolbar.title.setOnClickListener {
                 MenuBottomDialogFragment(MenuBottomDialogFragment.Type.PROFILE)
                     .show(childFragmentManager, null)
             }
             name.text = "Adidas men's blue denim"
             toolbar.icon.visibility = GONE
             Glide.with(photo)
                 .load(R.drawable.avatar8)
                 .circleCrop()
                 .into(photo)
             WidgetSellBarBinding.inflate(layoutInflater)
                 .also {
                     bottomBar.visibility = VISIBLE
                     bottomBar.addView(it.root)
                     it.startPrice.text = getString(R.string.pattern_price, String.format("%,d", 2000))
                     it.setDiscount.setOnClickListener {
                         PriceDialog(1500, PriceDialog.Type.DISCOUNT).show(childFragmentManager, null)
                     }
                 }
           /*  addMedia.setOnClickListener {
                 MenuBottomDialogFragment(MenuBottomDialogFragment.Type.GEO)
                     .show(childFragmentManager, null)
             }*/
         }
         adapter.setItems(buyer)
     }*/

    private fun registerReceiver(){
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(messageBroadCastReceiver,
        IntentFilter(MessagingService.MESSAGE_INTENT)
        )
    }

    private fun unregisterReceiver(){
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageBroadCastReceiver)
    }

    private fun showSell(dialog: DialogWrapper) {

        //currentDialogId = dialog.dialog.id
        user = dialog.dialog.user
        //adapter.avatarUri = dialog.dialog.user.avatar?.photo.toString()
        with(views) {
            product.setOnClickListener {
                findNavController()
                    .navigate(ChatFragmentDirections
                        .actionChatFragmentToProductDetailsFragment(
                            product = null, productId = dialog.product?.id.toString(), isSeller = true)) }
            toolbar.title.text = dialog.dialog.user.name
            toolbar.subtitle.text = "?? ???????? 2 ??. ??????????"
            toolbar.title.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_dropdown,
                0
            )
            toolbar.title.setOnClickListener {
                MenuBottomDialogFragment(type = MenuBottomDialogFragment.Type.PROFILE,
                avatar = dialog.dialog.user.avatar?.photo.toString())
                    .show(childFragmentManager, null)
            }
            name.text = dialog.product?.name
            toolbar.icon.visibility = GONE
            Glide.with(photo)
                .load(dialog.product?.photo?.first()?.photo)
                .circleCrop()
                .into(photo)
            WidgetSellBarBinding.inflate(layoutInflater)
                .also {
                    bottomBar.isVisible = requireArguments().getBoolean(FROM_ORDERS, false).not() &&
                            dialog.product?.status != 7 && dialog.product?.status != 8
                    bottomBar.addView(it.root)
                    it.startPrice.text =
                        getString(R.string.pattern_price, String.format("%,d", dialog.product?.priceNew?.toInt()))
                    it.setDiscount.setOnClickListener {
                        PriceDialog(dialog.product?.priceNew?.toInt() ?: 0 , PriceDialog.Type.DISCOUNT).show(
                            childFragmentManager,
                            null
                        )
                    }
                }
            addMedia.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.MEDIA,
                    mediaUris.isNullOrEmpty().not(), avatar = null
                )
                    .show(childFragmentManager, null)
            }
        }

        val messages = getMessages(dialog.messages.orEmpty(), dialog.product?.statusUser)
        adapter.setItems(messages.orEmpty().reversed())
        views.list.scrollToPosition(adapter.itemCount - 1)

    }

    /*private fun showSupport() {
        //adapter.avatarUri = R.drawable.ic_headphones
        with(views) {
            toolbar.icon.setPadding(7.dp, 7.dp, 7.dp, 7.dp)
            toolbar.title.setText(R.string.chat_support_title)
            toolbar.subtitle.setText(R.string.chat_support_description)
            Glide.with(toolbar.icon)
                .load(R.drawable.ic_headphones)
                .circleCrop()
                .into(toolbar.icon)
            product.visibility = GONE
            productDivider.visibility = GONE
            addMedia.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.MEDIA)
                    .show(childFragmentManager, null)
            }
        }
        adapter.setItems(support)
    }*/

    private fun showSupport(dialog: DialogWrapper) {
        with(views) {
            toolbar.icon.setPadding(7.dp, 7.dp, 7.dp, 7.dp)
            toolbar.title.setText(R.string.chat_support_title)
            toolbar.subtitle.setText(R.string.chat_support_description)
            Glide.with(toolbar.icon)
                .load(R.drawable.ic_headphones)
                .circleCrop()
                .into(toolbar.icon)
            product.visibility = GONE
            productDivider.visibility = GONE
            addMedia.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.MEDIA,
                    mediaUris.isNullOrEmpty().not(), avatar = null
                )
                    .show(childFragmentManager, null)
            }
        }

        val messages = getMessages(dialog.messages.orEmpty(), null)

        adapter.setItems(messages.orEmpty().reversed())
        views.list.scrollToPosition(adapter.itemCount - 1)
    }

    /*private fun showDeal(){
        adapter.avatarId = R.drawable.user3
        with(views){
            toolbar.title.text = "?????????? ??."
            toolbar.subtitle.text = "?? ???????? 2 ??. ??????????"
            toolbar.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_dropdown, 0)
            toolbar.title.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.PROFILE)
                    .show(childFragmentManager, null)
            }
            name.text = "???????????? salamander 35"
            toolbar.icon.visibility = GONE
            Glide.with(photo)
                .load(R.drawable.avatar1)
                .circleCrop()
                .into(photo)
            bottomBar.visibility = VISIBLE
            WidgetBuyBarBinding.inflate(layoutInflater)
                .also {
                    bottomBar.addView(it.root)
                    it.myPrice.setOnClickListener {
                        PriceDialog(3890, PriceDialog.Type.PRICE).show(childFragmentManager, null)
                    }
                    it.buy.setOnClickListener { buy() }
                }
            addMedia.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.GEO)
                    .show(childFragmentManager, null)
            }
        }
        adapter.setItems(seller)
    }*/

    private fun showDeal(dialog: DialogWrapper) {
        user = dialog.dialog.user
        adapter.avatarUri = dialog.dialog.user.avatar?.photo.toString()
        with(views) {
            product.setOnClickListener {
                findNavController()
                    .navigate(ChatFragmentDirections
                        .actionChatFragmentToProductDetailsFragment(
                            product = null, productId = dialog.product?.id.toString(), isSeller = false)) }
            toolbar.title.text = dialog.dialog.user.name
            toolbar.subtitle.text = "?? ???????? 2 ??. ??????????"
            toolbar.title.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_dropdown,
                0
            )
            toolbar.title.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.PROFILE, avatar =
                dialog.dialog.user.avatar?.photo.toString(),
                    showReviewBtn = dialog.product?.statusUser?.order?.status == 1 &&
                dialog.product.statusUser.order.idUser == currentUserId)
                    .show(childFragmentManager, null)
            }
            name.text = dialog.product?.name
            toolbar.icon.visibility = GONE
            Glide.with(photo)
                .load(dialog.product?.photo?.first()?.photo)
                .circleCrop()
                .into(photo)
            bottomBar.isVisible = requireArguments().getBoolean(FROM_ORDERS, false).not() &&
                    dialog.product?.status != 7 && dialog.product?.status != 8


            WidgetBuyBarBinding.inflate(layoutInflater)
                .also {
                    bottomBar.addView(it.root)
                    it.myPrice.setOnClickListener {
                        PriceDialog(dialog.product?.priceNew?.toInt() ?: 0, PriceDialog.Type.PRICE).show(childFragmentManager, null)
                    }
                    it.buy.setOnClickListener { buy() }
                }
            addMedia.setOnClickListener {
                MenuBottomDialogFragment(
                    MenuBottomDialogFragment.Type.MEDIA,
                    mediaUris.isNullOrEmpty().not()
                )
                    .show(childFragmentManager, null)
            }
        }

        val messages = getMessages(dialog.messages.orEmpty(), dialog.product?.statusUser)
        adapter.setItems(messages.orEmpty().reversed())
        views.list.scrollToPosition(adapter.itemCount - 1)
        val buyMessage = dialog.messages?.find { it.buySuggest != null }
        if(buyMessage != null && buyMessage.buySuggest?.status != 2){
            views.bottomBar.removeAllViews()
            WidgetDealCancelBinding.inflate(layoutInflater)
                .also {
                    views.bottomBar.addView(it.root)
                    it.cancel.setOnClickListener { cancelBuy(buyMessage.buySuggest?.id!!) }
                }

        }
    }

    private fun showDialog(dialog: DialogWrapper) {
        when (requireArguments().getString(Chat::class.java.simpleName)) {
            Chat.Deal::class.java.simpleName -> showDeal(dialog)
            Chat.Personal::class.java.simpleName -> showSell(dialog)
            Chat.Support::class.java.simpleName -> showSupport(dialog)
        }
    }


    private fun createDealChat(userId: Long, productId: Long, withBuySuggest: Boolean) {
        presenter.createDialog(userId, productId, withBuySuggest)

    }

    private fun getDialog(id: Long) {
        presenter.getDialogById(id)
    }

    private fun initFilePicker() {
        filePicker =
            registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
                presenter.uploadPhotoVideo(uris, "")

            }
    }

    private fun getMessages(list: List<MessageModel>, statusUser: ProductUserStatus?): List<Message> {
        Log.d("mylog", list.toString())
        val dates = list.filter { it.saleSuggest?.status != 0 }.mapNotNull {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date * 1000
            calendar

        }
        val messages = list.mapNotNull {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date * 1000
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            when {
                it.idSender == currentUserId && it.text != null && (it.photo == null && it.video == null) -> Message.My(
                    "$hour:$minute",
                    if(it.is_read == 1) R.drawable.ic_check_double else R.drawable.ic_check,
                    it.text.orEmpty()
                )
                (it.photo != null || it.video != null) && it.idSender == currentUserId -> {
                    val photos =
                        it.photo?.mapIndexed { index, photo -> Media.Photo(photo.photo, index + 1) }
                            .orEmpty()
                    val videos = it.video?.mapIndexed { index, video ->
                        Media.Video(
                            video.video,
                            photos.size + index + 1
                        )
                    }.orEmpty()
                    Log.d("MyDebug", "photos size = ${photos.size}")
                    val media = photos + videos
                    Message.MyMedia(
                        "$hour:$minute",
                        if(it.is_read == 1) R.drawable.ic_check_double else R.drawable.ic_check,
                        media.toTypedArray(),
                        it.text
                    )
                }
                it.idSender != currentUserId && it.text != null && (it.photo == null && it.video == null) -> Message.Companion(
                    listOf(
                        Msg(
                            "$hour:$minute",
                            it.text.orEmpty()
                        )
                    )
                )
                (it.photo != null || it.video != null) && it.idSender != currentUserId -> {
                    val photos =
                        it.photo?.mapIndexed { index, photo -> Media.Photo(photo.photo, index + 1) }
                            .orEmpty()
                    val videos = it.video?.mapIndexed { index, video ->
                        Media.Video(
                            video.video,
                            photos.size + index + 1
                        )
                    }.orEmpty()
                    Log.d("MyDebug", "photos size = ${photos.size}")
                    val media = photos + videos
                    Message.CompanionMedia(
                        "$hour:$minute",
                        R.drawable.ic_check_double,
                        media.toTypedArray(),
                        it.text
                    )
                }
                it.idSender == currentUserId && it.buySuggest != null -> {
                    val text = when(it.buySuggest.status){
                        0 -> "?????? ?????? ????????????"
                        1 -> "????????????????"
                        2 -> "????????????????"
                        else -> ""
                    }
                    val color = if(it.buySuggest.status == 1)
                        ResourcesCompat.getColor(resources, R.color.green, null)
                    else
                        ResourcesCompat.getColor(resources, R.color.gray2, null)
                    val submitIcon = if(it.buySuggest.status == 1)
                        R.drawable.ic_check_round_fill
                    else
                        R.drawable.ic_bag_filled_green
                    Message.Special.BuyRequest("$hour:$minute",
                        if(it.is_read == 1) R.drawable.ic_check_double else R.drawable.ic_check,
                        text,
                        color,
                        submitIcon)
                }
                it.idSender == currentUserId && it.priceSuggest != null -> {
                    val text = when(it.priceSuggest.status){
                        0 -> "?????? ?????? ????????????"
                        1 -> "????????????????"
                        2 -> "????????????????"
                        else -> ""
                    }
                    val color = if(it.priceSuggest.status == 1)
                        ResourcesCompat.getColor(resources, R.color.green, null)
                    else
                        ResourcesCompat.getColor(resources, R.color.gray2, null)
                    val drawable = when(it.priceSuggest.status){
                        0 -> null
                        1 -> R.drawable.ic_check_round_fill
                        2 -> R.drawable.ic_x
                        else -> null
                    }
                    Message.Special.PriceRequest("$hour:$minute",
                        if(it.is_read == 1) R.drawable.ic_check_double else R.drawable.ic_check,
                        it.priceSuggest.value ?: "",
                        text,
                        color,
                        drawable)
                }
                it.saleSuggest?.status == 1 -> Message.Special.PriceAccepted( it.saleSuggest.value.toString())
                it.saleSuggest?.status == 0 -> null
                it.buySuggest?.status == 0 && it.idSender != currentUserId -> Message.Special.DealRequest("$hour:$minute", currentProductPrice?.toInt() ?: 0, it.buySuggest.id)
                it.buySuggest?.status == 1 && it.idSender != currentUserId -> Message.Special.DealAccept("$hour:$minute")
                it.buySuggest?.status == 2 && it.idSender != currentUserId -> Message.Special.DealCancel("$hour:$minute")
                it.priceSuggest?.status == 1 && it.idSender != currentUserId -> Message.Special.PriceSubmitted("$hour:$minute",  it.priceSuggest.value!!)
                it.priceSuggest?.status == 0 && it.idSender != currentUserId -> Message.Special.PriceSubmit("$hour:$minute", it.priceSuggest.value!!, it.priceSuggest.id)
                it.priceSuggest?.status == 2 && it.idSender != currentUserId -> Message.Special.PriceCanceled("$hour:$minute", it.priceSuggest.value!!)
                else -> null
            }
        }

        val priceDetailsIndexes = mutableListOf<Int>()
        val buyDetailsIndexes = mutableListOf<Int>()

         list.filter { it.saleSuggest?.status != 0 }.forEachIndexed { index, messageModel ->
             if(messageModel.priceSuggest != null && messageModel.priceSuggest.status == 1)
                 priceDetailsIndexes.add(index)
             if(messageModel.buySuggest != null && messageModel.buySuggest.status == 1) //&& messageModel.idSender == currentUserId)
                 buyDetailsIndexes.add(index)

        }

        val datedMessages = mutableListOf<Message>()
        messages.forEachIndexed { index, message ->
            val currentDate = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)
            val currentMonth = currentDate.get(Calendar.MONTH) + 1
            val currentYear = currentDate.get(Calendar.YEAR)

            val priceAccepted = if(priceDetailsIndexes.find { it == index } != null)
                Message.Special.PriceAccepted(list.filter { it.saleSuggest?.status != 0 }[index].priceSuggest?.value.toString())
            else null

            val buyAccepted = if(buyDetailsIndexes.find { it == index } != null)
                //Message.Special.BuyDetails
                    Message.Special.DealDetails(statusUser?.order?.id ?: -1)
            else null


            val day = dates[index].get(Calendar.DAY_OF_MONTH)
                val month = dates[index].get(Calendar.MONTH) + 1
                val year = dates[index].get(Calendar.YEAR)
                val nextDay = if(index + 1 <= dates.lastIndex)
                    dates[index + 1].get(Calendar.DAY_OF_MONTH)
                else day
                val nextMonth = if(index + 1 <= dates.lastIndex)
                    dates[index + 1].get(Calendar.MONTH) + 1
                else month
                val nextYear = if(index + 1 <= dates.lastIndex)
                    dates[index + 1].get(Calendar.YEAR)
                else year
                if((day != nextDay || month != nextMonth || year != nextYear) || index == dates.lastIndex){
                    Log.d("mylog", "showDate")
                    val text = if(currentDay == day && currentMonth == month && currentYear == year)
                        "??????????????"
                    else
                        "$day.$month.$year"
                    priceAccepted?.let { datedMessages.add(it) }
                    buyAccepted?.let { datedMessages.add(it) }
                    datedMessages.add(message)
                    datedMessages.add(Message.Divider(text))
                }else{
                    Log.d("mylog", "index= $index, lastIndex: ${dates.lastIndex}")
                    buyAccepted?.let { datedMessages.add(it) }
                    priceAccepted?.let { datedMessages.add(it) }
                    datedMessages.add(message)
                }


        }
        Log.d("mylog", datedMessages.toString())

        return datedMessages
    }

    //todo remove stubs
    /*private val support = listOf(
        Message.Divider(R.string.today),
        Message.My("13:18", R.drawable.ic_check_double, "????????????????????????! ?????? ???????????? ?????????????????? ??????????!"),
        Message.Companion(listOf(Msg("13:19","???????????? ????????! ???????????????????? ???????????????? ????????????"))),
        Message.MyMedia("13:18", R.drawable.ic_check_double, arrayOf(
            Media.Photo(R.drawable.user1),
            Media.Video(R.drawable.user2),
            Media.Photo(R.drawable.user4),
            Media.Photo(R.drawable.ic_lacoste, 2)
        )),
        Message.MyMedia("13:18", R.drawable.ic_check_double, arrayOf(
            Media.Photo(R.drawable.user1),
            Media.Photo(R.drawable.user5)
        ))
    )*/

    private val buyer = listOf(
        //Message.Divider(R.string.today),
        Message.Companion(
            listOf(
                Msg("13:16", "?????? ???????????????? ???????????"),
                Msg("13:19", "????????????????????????! ???????????? ????????????????, ?????????? ???????????? ???????????? ???????????????")
            )
        ),
        Message.My(
            "13:18",
            R.drawable.ic_check_double,
            "???????????? ????????! ?????? ?????? ???????????? ?????????????? ????????????"
        ),
        Message.Special.DealRequest("13:19", 0, 0)
    )
    private val accepted = buyer
        .map { if (it is Message.Special.DealRequest) Message.Special.DealAccept("13:19") else it }
        .toMutableList()
        .also { it.add(Message.Special.DealDetails(-1)) }

    private val seller = listOf(
        //Message.Divider(R.string.today),
        Message.My("13:18", R.drawable.ic_check_double, "????????????????????????! ?????? ????????????????? "),
        Message.Companion(listOf(Msg("13:18", "???????????? ????????! ?????? ?????? ???????????? ?????????????? ????????????")))
    )

    private val seller2 = seller + Message.Special.BuyRequest("13:20", R.drawable.ic_check_single, "", 0, 0)

    private val seller3 = seller2 + Message.Special.BuyDetails

    private val seller4 = seller3 + Message.Special.GeoDetails

    private val stubAcceptDealRunnable: Runnable get() = Runnable { adapter.setItems(accepted) }

    private fun buy() {
        /*views.bottomBar.removeAllViews()
        WidgetDealCancelBinding.inflate(layoutInflater)
            .also {
                views.bottomBar.addView(it.root)
                //it.cancel.setOnClickListener { cancelBuy() }
                cancelWidgetBinding = it
            }
        /*adapter.setItems(seller2)
        views.send.setOnClickListener {
            adapter.setItems(seller3)
            listenGeo = true
        }*/

        presenter.sendBuyRequest(currentDialogId ?: 0)
        val timeStr = "${Calendar.getInstance().get(Calendar.HOUR_OF_DAY)}:${Calendar.getInstance().get(Calendar.MINUTE)}"
        adapter.addMessage(Message.Special.BuyRequest(timeStr, R.drawable.ic_check, "?????? ?????? ????????????", ResourcesCompat.getColor(resources, R.color.gray2, null), R.drawable.ic_bag_filled_green ))
        views.list.scrollToPosition(adapter.itemCount - 1)*/
        if(this::user.isInitialized && user.isBlackList == true){
            error("???????????????????? ?????? ????????????????????????")
            return
        }
        presenter.buyProduct(currentProductId!!, currentUserId!!)

    }

    private fun cancelBuy(suggestId: Long) {
        views.bottomBar.removeAllViews()
        //adapter.setItems(seller)
        WidgetBuyBarBinding.inflate(layoutInflater)
            .also {
                views.bottomBar.addView(it.root)
                it.myPrice.setOnClickListener {
                    PriceDialog(currentProductPrice?.toInt() ?: 0, PriceDialog.Type.PRICE).show(childFragmentManager, null)
                }
                it.buy.setOnClickListener { buy() }
            }
        //views.send.setOnClickListener { /* remove */ }
        //listenGeo = false
        presenter.cancelBuyRequest(currentDialogId ?: 0, suggestId )
    }

    private fun setClickListeners() {
        views.send.setOnClickListener {
            when {
                views.messageEditText.text.isNullOrBlank() && mediaUris.isNullOrEmpty().not() -> {
                    sendOnlyMedia()
                }
                views.messageEditText.text.isNullOrBlank().not() && mediaUris.isNullOrEmpty() -> {
                    sendOnlyText()
                }
                views.messageEditText.text.isNullOrBlank().not() && mediaUris.isNullOrEmpty()
                    .not() -> {
                    sendTextMedia(views.messageEditText.text.toString())
                }
            }

        }
    }

    private fun requestPrice(newPrice: String) = listOf<Message>(
        *seller.toTypedArray(),
        //Message.Special.PriceRequest("13:20", R.drawable.ic_check_double, newPrice),
        //Message.Special.PriceAccepted("${getString(R.string.priceDownTo)} $newPrice")
    )

    private fun loadMediaUris() {
        presenter.loadMediaUris()
    }

    private fun sendOnlyMedia() {
        if(this::user.isInitialized && user.isBlackList == true){
            error("???????????????????? ?????? ????????????????????????")
            return
        }

        val mediaPhoto = mediaUris!!.filter {
            requireContext().contentResolver.getType(it)?.substringAfter('/') != "mp4"
        }
            .mapIndexed { index, uri -> Media.Photo(uri.toString(), index + 1) }
        val mediaVideo = mediaUris!!.filter {
            requireContext().contentResolver.getType(it)?.substringAfter('/') == "mp4"
        }
            .mapIndexed { index, uri -> Media.Video(uri.toString(), mediaPhoto.size + index + 1) }
        val media = mediaPhoto + mediaVideo

        if(adapter.isTodayMessages().not())
            adapter.addMessage(Message.Divider("??????????????"))

        val calendar = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)


        adapter.addMessage(
            Message.MyMedia(
                "$hour:$minute",
                R.drawable.empty,
                media.toTypedArray(),
                null
            )
        )
        views.list.scrollToPosition(adapter.itemCount - 1)
        presenter.uploadPhotoVideo(mediaUris!!, "")
        presenter.clearMediaUris()
        mediaUris = null
    }

    private fun sendOnlyText() {
        if(this::user.isInitialized && user.isBlackList == true){
            error("???????????????????? ?????? ????????????????????????")
            return
        }
        val calendar = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        presenter.addMessage(views.messageEditText.text.toString())
        if(adapter.isTodayMessages().not())
            adapter.addMessage(Message.Divider("??????????????"))
        adapter.addMessage(
            Message.My(
                "$hour:$minute",
                R.drawable.empty,
                views.messageEditText.text.toString()
            )
        )
        views.list.scrollToPosition(adapter.itemCount - 1)
        views.messageEditText.text.clear()
    }

    private fun sendTextMedia(message: String) {
        if(this::user.isInitialized && user.isBlackList == true){
            error("???????????????????? ?????? ????????????????????????")
            return
        }
        val mediaPhoto = mediaUris!!.filter {
            requireContext().contentResolver.getType(it)?.substringAfter('/') != "mp4"
        }
            .mapIndexed { index, uri -> Media.Photo(uri.toString(), index + 1) }
        val mediaVideo = mediaUris!!.filter {
            requireContext().contentResolver.getType(it)?.substringAfter('/') == "mp4"
        }
            .mapIndexed { index, uri -> Media.Video(uri.toString(), mediaPhoto.size + index + 1) }
        val media = mediaPhoto + mediaVideo

        val calendar = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        if(adapter.isTodayMessages().not())
            adapter.addMessage(Message.Divider("??????????????"))


        adapter.addMessage(
            Message.MyMedia(
                "$hour:$minute",
                R.drawable.empty,
                media.toTypedArray(),
                message
            )
        )
        views.list.scrollToPosition(adapter.itemCount - 1)
        presenter.uploadPhotoVideo(mediaUris!!, message)
        presenter.clearMediaUris()
        mediaUris = null
    }

    private fun showLoading(loading: Boolean){
        views.loader.isVisible = loading
    }

    override fun selectAction(item: MenuBottomDialogFragment.MenuItem) {
        //if(!listenGeo) return
        if (item.titleId == R.string.chat_menu_setGeoDeal) adapter.setItems(seller4)
        if (item.titleId == R.string.chat_menu_delete) presenter.deleteDialog(currentDialogId ?: -1)
        if (item.titleId == R.string.chat_menu_profile)
            findNavController().navigate(
                ChatFragmentDirections.actionChatFragmentToSellerProfileFragment(
                   user = user,
                   toReviews =  false
                )
            )
        if (item.titleId == R.string.chat_menu_feedback)
            findNavController().navigate(
                ChatFragmentDirections.actionChatFragmentToSellerProfileFragment(
                  user =  user,
                   toReviews =  true
                )
            )
        if (item.titleId == R.string.chat_menu_addMedia)
            findNavController().navigate(
                ChatFragmentDirections.actionChatFragmentToFeedbackPhotoFragment(
                    true
                )
            )
        if(item.titleId == R.string.chat_menu_block)
            presenter.blockUser(user.id)
    }

    override fun applyNewPrice(newPrice: String) {
        //adapter.setItems(requestPrice(newPrice))
        if(this::user.isInitialized && user.isBlackList == true){
            error("???????????????????? ?????? ????????????????????????")
            return
        }
        val calendar = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        presenter.sendPriceSuggest(currentDialogId ?: 0, newPrice.toInt())
        if(adapter.isTodayMessages().not())
            adapter.addMessage(Message.Divider("??????????????"))
        adapter.addMessage(Message.Special.PriceRequest("$hour:$minute", R.drawable.ic_check, newPrice, "?????? ?????? ????????????", ResourcesCompat.getColor(resources, R.color.gray2, null), null))
        views.list.scrollToPosition(adapter.itemCount - 1)
    }

    override fun applyDiscount(discount: String) {
        if(this::user.isInitialized && user.isBlackList == true){
            error("???????????????????? ?????? ????????????????????????")
            return
        }
         adapter.addMessage(Message.Special.PriceAccepted(discount))
         val dialogId = requireArguments().getLong(DIALOG_ID_KEY)
         presenter.offerDiscount(ChatFunctionInfo(dialogId = dialogId, value = discount.toInt()))
    }

    companion object {
        const val USER_ID_KEY = "user_id"
        const val PRODUCT_ID_KEY = "product_id"
        const val DIALOG_ID_KEY = "dialog_id"
        const val FROM_ORDERS = "from orders"
    }

    override fun loading() {
        showLoading(true)
    }

    override fun dialogsLoaded(dialogs: List<Chat>) {

    }

    override fun dialogLoaded(dialog: DialogWrapper) {
        showLoading(false)
        currentDialogId = dialog.dialog.id
        currentProductPrice = dialog.product?.priceNew
        currentProductId = dialog.product?.id
        user = dialog.dialog.user
        adapter.avatarUri = dialog.dialog.user.avatar?.photo.toString()
        showDialog(dialog)
        presenter.readMessages(dialog.dialog.id)
    }

    override fun dialogCreated(dialogId: CreatedDialogId) {

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun currentUserIdLoaded(id: Long) {
        showLoading(false)
        currentUserId = id
    }

    override fun messageSent(message: MessageModel) {
        showLoading(false)
        Toast.makeText(requireContext(), "?????????????????? ????????????????????", Toast.LENGTH_SHORT).show()
        //val messages = getMessages(listOf(message))
        adapter.updateMessage()

    }

    override fun dialogDeleted() {
        showLoading(false)
        Toast.makeText(requireContext(), "???????????? ????????????", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun photoVideoLoaded() {
        showLoading(false)
        Toast.makeText(requireContext(), "?????????? ??????????????????", Toast.LENGTH_SHORT).show()
        adapter.updateMessage()
    }

    override fun showDialogCount(type: String, count: Int) {

    }

    override fun mediaUrisLoaded(mediaUris: List<Uri>?) {
        showLoading(false)
        this.mediaUris = mediaUris
    }

    override fun actionMessageSent(info: ChatFunctionInfo, type: MessageActionType) {
        showLoading(false)
        when(type){
            MessageActionType.BUY_REQUEST_SUGGEST -> {
                Toast.makeText(requireContext(), "???????????? ???? ?????????????? ??????????????????", Toast.LENGTH_SHORT).show()
                //cancelWidgetBinding.cancel.setOnClickListener { cancelBuy(info.suggestId!!) }

            }
            MessageActionType.BUY_REQUEST_CANCEL -> {
                Toast.makeText(requireContext(), "???????????? ????????????????", Toast.LENGTH_SHORT).show()

            }
            MessageActionType.PRICE_SUGGEST -> Toast.makeText(requireContext(), "?????????????????????? ???????? ????????????????????", Toast.LENGTH_SHORT).show()
            MessageActionType.DISCOUNT_REQUEST_SUBMIT -> Toast.makeText(requireContext(), "???????????? ??????????????", Toast.LENGTH_LONG).show()
            MessageActionType.BUY_REQUEST_SUBMIT -> {
                Toast.makeText(requireContext(), "?????????????? ????????????????", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.salesActiveFragment)
            }
            MessageActionType.PRICE_REQUEST_SUBMIT -> Toast.makeText(requireContext(), "???????? ????????????????", Toast.LENGTH_LONG).show()
            MessageActionType.PRICE_REQUEST_CANCEL -> Toast.makeText(requireContext(), "???????? ????????????????", Toast.LENGTH_LONG).show()
        }
    }

    override fun showUnreadMessagesStatus(show: Boolean) {
        (activity as MainActivity).showUnreadMessagesIcon(show)
    }

    override fun showUnreadTab(tab: Int, unread: Boolean) {

    }

    override fun productAddedToCart(product: Product, cartId: Long) {
        findNavController().navigate(ChatFragmentDirections.actionChatFragmentToCreateOrderFragment(product, cartId))
    }
}