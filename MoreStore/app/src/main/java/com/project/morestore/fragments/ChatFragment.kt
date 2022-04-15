package com.project.morestore.fragments

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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace
import dev.jorik.stub.defToast
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
    private lateinit var filePicker: ActivityResultLauncher<Array<String>>
    private val adapter = MessagesAdapter(
        {
            stubAcceptDealRunnable.run()
            views.bottomBar.visibility = GONE
        },
        { requireContext().defToast(R.string.cancel) },
        { media ->
            val uris =
                media.map { if (it is Media.Photo) it.photoUri else (it as Media.Video).videoUri }
            findNavController().navigate(
                R.id.action_chatFragment_to_mediaFragment,
                bundleOf(MediaFragment.PHOTOS to uris.toTypedArray())
            )
        }
    )
    private var listenGeo = false
    private lateinit var user: User
    private var mediaUris: List<Uri>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChatBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        with(views) {
            toolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
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
                    createDealChat(userId, productId)
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

    /* private fun showSell(){
        // adapter.avatarUri = R.drawable.user2
         with(views){
             toolbar.title.text = "Влада Т."
             toolbar.subtitle.text = "В сети 2 ч. назад"
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

    private fun showSell(dialog: DialogWrapper) {
        currentDialogId = dialog.dialog.id
        user = dialog.dialog.user
        adapter.avatarUri = dialog.dialog.user.avatar?.photo.toString()
        with(views) {
            toolbar.title.text = dialog.dialog.user.name
            toolbar.subtitle.text = "В сети 2 ч. назад"
            toolbar.title.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_dropdown,
                0
            )
            toolbar.title.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.PROFILE)
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
                    bottomBar.visibility = VISIBLE
                    bottomBar.addView(it.root)
                    it.startPrice.text =
                        getString(R.string.pattern_price, String.format("%,d", 2000))
                    it.setDiscount.setOnClickListener {
                        PriceDialog(1500, PriceDialog.Type.DISCOUNT).show(
                            childFragmentManager,
                            null
                        )
                    }
                }
            addMedia.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.MEDIA,
                    mediaUris.isNullOrEmpty().not()
                )
                    .show(childFragmentManager, null)
            }
        }

        val messages = getMessages(dialog.messages.orEmpty())

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
                    mediaUris.isNullOrEmpty().not()
                )
                    .show(childFragmentManager, null)
            }
        }

        val messages = getMessages(dialog.messages.orEmpty())

        adapter.setItems(messages.orEmpty().reversed())
        views.list.scrollToPosition(adapter.itemCount - 1)
    }

    /*private fun showDeal(){
        adapter.avatarId = R.drawable.user3
        with(views){
            toolbar.title.text = "Елена Б."
            toolbar.subtitle.text = "В сети 2 ч. назад"
            toolbar.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_dropdown, 0)
            toolbar.title.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.PROFILE)
                    .show(childFragmentManager, null)
            }
            name.text = "Сапоги salamander 35"
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
        currentDialogId = dialog.dialog.id
        currentProductPrice = dialog.product?.priceNew
        user = dialog.dialog.user
        adapter.avatarUri = dialog.dialog.user.avatar?.photo.toString()
        with(views) {
            toolbar.title.text = dialog.dialog.user.name
            toolbar.subtitle.text = "В сети 2 ч. назад"
            toolbar.title.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_dropdown,
                0
            )
            toolbar.title.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.PROFILE)
                    .show(childFragmentManager, null)
            }
            name.text = dialog.product?.name
            toolbar.icon.visibility = GONE
            Glide.with(photo)
                .load(dialog.product?.photo?.first()?.photo)
                .circleCrop()
                .into(photo)
            bottomBar.visibility = VISIBLE
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

        val messages = getMessages(dialog.messages.orEmpty())
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


    private fun createDealChat(userId: Long, productId: Long) {
        presenter.createDialog(userId, productId)

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

    private fun getMessages(list: List<MessageModel>): List<Message> {
        Log.d("mylog", list.toString())
        val dates = list.filter { it.saleSuggest == null }.mapNotNull {
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
                    R.drawable.ic_check_double,
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
                        R.drawable.ic_check_double,
                        media.toTypedArray(),
                        it.text
                    )
                }
                it.idSender != currentUserId && it.text != null -> Message.Companion(
                    listOf(
                        Msg(
                            "$hour:$minute",
                            it.text.orEmpty()
                        )
                    )
                )
                it.idSender == currentUserId && it.buySuggest != null -> {
                    val text = when(it.buySuggest.status){
                        0 -> "Еще нет ответа"
                        1 -> "Одобрено"
                        2 -> "Отменено"
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
                    Message.Special.BuyRequest("$hour:$minute", R.drawable.ic_check_double, text, color, submitIcon)
                }
                it.idSender == currentUserId && it.priceSuggest != null -> {
                    val text = when(it.priceSuggest.status){
                        0 -> "Еще нет ответа"
                        1 -> "Одобрено"
                        2 -> "Отменено"
                        else -> ""
                    }
                    val color = if(it.priceSuggest.status == 1)
                        ResourcesCompat.getColor(resources, R.color.green, null)
                    else
                        ResourcesCompat.getColor(resources, R.color.gray2, null)
                    Message.Special.PriceRequest("$hour:$minute", R.drawable.ic_check_double, it.priceSuggest.value ?: "", text, color, 0)
                }
                else -> null
            }
        }

        val priceDetailsIndexes = mutableListOf<Int>()
        val buyDetailsIndexes = mutableListOf<Int>()

         list.filter { it.saleSuggest == null }.forEachIndexed { index, messageModel ->
             if(messageModel.priceSuggest != null && messageModel.priceSuggest.status == 1)
                 priceDetailsIndexes.add(index)
             if(messageModel.buySuggest != null && messageModel.buySuggest.status == 1)
                 buyDetailsIndexes.add(index)

        }

        val datedMessages = mutableListOf<Message>()
        messages.forEachIndexed { index, message ->
            val currentDate = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)
            val currentMonth = currentDate.get(Calendar.MONTH) + 1
            val currentYear = currentDate.get(Calendar.YEAR)

            val priceAccepted = if(priceDetailsIndexes.find { it == index } != null)
                Message.Special.PriceAccepted(list.filter { it.saleSuggest == null }[index].priceSuggest?.value.toString())
            else null

            val buyAccepted = if(buyDetailsIndexes.find { it == index } != null)
                Message.Special.BuyDetails()
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
                        "сегодня"
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
        Message.My("13:18", R.drawable.ic_check_double, "Здравствуйте! Мне пришел порванный товар!"),
        Message.Companion(listOf(Msg("13:19","Добрый день! Пожалуйста уточните детали"))),
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
                Msg("13:16", "Ещё продаёте товар?"),
                Msg("13:19", "Здравствуйте! Хотела спросить, какую скидку можете сделать?")
            )
        ),
        Message.My(
            "13:18",
            R.drawable.ic_check_double,
            "Добрый день! Для вас готова сделать скидку"
        ),
        Message.Special.DealRequest("13:19")
    )
    private val accepted = buyer
        .map { if (it is Message.Special.DealRequest) Message.Special.DealAccept("13:19") else it }
        .toMutableList()
        .also { it.add(Message.Special.DealDetails()) }

    private val seller = listOf(
        //Message.Divider(R.string.today),
        Message.My("13:18", R.drawable.ic_check_double, "Здравствуйте! Еще продаете? "),
        Message.Companion(listOf(Msg("13:18", "Добрый день! Для вас готова сделать скидку")))
    )

    private val seller2 = seller + Message.Special.BuyRequest("13:20", R.drawable.ic_check_single, "", 0, 0)

    private val seller3 = seller2 + Message.Special.BuyDetails()

    private val seller4 = seller3 + Message.Special.GeoDetails()

    private val stubAcceptDealRunnable: Runnable get() = Runnable { adapter.setItems(accepted) }

    private fun buy() {
        views.bottomBar.removeAllViews()
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
        adapter.addMessage(Message.Special.BuyRequest(timeStr, R.drawable.ic_check_double, "Еще нет ответа", ResourcesCompat.getColor(resources, R.color.gray2, null), R.drawable.ic_bag_filled_green ))
        views.list.scrollToPosition(adapter.itemCount - 1)
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

            /* presenter.addMessage(views.messageEditText.text.toString())
             adapter.addMessage(Message.My("13:00", R.drawable.ic_check_double, views.messageEditText.text.toString()))
             views.list.scrollToPosition(adapter.itemCount - 1)
             views.messageEditText.text.clear()*/
        }

        //views.addMedia.setOnClickListener {
        //  filePicker.launch(arrayOf("image/*", "video/mp4"))
        //}
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
        val mediaPhoto = mediaUris!!.filter {
            requireContext().contentResolver.getType(it)?.substringAfter('/') != "mp4"
        }
            .mapIndexed { index, uri -> Media.Photo(uri.toString(), index + 1) }
        val mediaVideo = mediaUris!!.filter {
            requireContext().contentResolver.getType(it)?.substringAfter('/') == "mp4"
        }
            .mapIndexed { index, uri -> Media.Video(uri.toString(), mediaPhoto.size + index + 1) }
        val media = mediaPhoto + mediaVideo

        adapter.addMessage(
            Message.MyMedia(
                "13:00",
                R.drawable.ic_check_double,
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
        val calendar = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        presenter.addMessage(views.messageEditText.text.toString())
        if(adapter.isTodayMessages().not())
            adapter.addMessage(Message.Divider("сегодня"))
        adapter.addMessage(
            Message.My(
                "$hour:$minute",
                R.drawable.ic_check_double,
                views.messageEditText.text.toString()
            )
        )
        views.list.scrollToPosition(adapter.itemCount - 1)
        views.messageEditText.text.clear()
    }

    private fun sendTextMedia(message: String) {
        val mediaPhoto = mediaUris!!.filter {
            requireContext().contentResolver.getType(it)?.substringAfter('/') != "mp4"
        }
            .mapIndexed { index, uri -> Media.Photo(uri.toString(), index + 1) }
        val mediaVideo = mediaUris!!.filter {
            requireContext().contentResolver.getType(it)?.substringAfter('/') == "mp4"
        }
            .mapIndexed { index, uri -> Media.Video(uri.toString(), mediaPhoto.size + index + 1) }
        val media = mediaPhoto + mediaVideo

        adapter.addMessage(
            Message.MyMedia(
                "13:00",
                R.drawable.ic_check_double,
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
                    user,
                    false
                )
            )
        if (item.titleId == R.string.chat_menu_feedback)
            findNavController().navigate(
                ChatFragmentDirections.actionChatFragmentToSellerProfileFragment(
                    user,
                    true
                )
            )
        if (item.titleId == R.string.chat_menu_addMedia)
            findNavController().navigate(
                ChatFragmentDirections.actionChatFragmentToFeedbackPhotoFragment(
                    true
                )
            )
    }

    override fun applyNewPrice(newPrice: String) {
        //adapter.setItems(requestPrice(newPrice))
        presenter.sendPriceSuggest(currentDialogId ?: 0, newPrice.toInt())
        adapter.addMessage(Message.Special.PriceRequest("13:00", R.drawable.ic_check_double, newPrice, "Еще нет ответа", ResourcesCompat.getColor(resources, R.color.gray2, null), 0))
        views.list.scrollToPosition(adapter.itemCount - 1)
    }

    companion object {
        const val USER_ID_KEY = "user_id"
        const val PRODUCT_ID_KEY = "product_id"
        const val DIALOG_ID_KEY = "dialog_id"
    }

    override fun loading() {
        showLoading(true)

    }

    override fun dialogsLoaded(dialogs: List<Chat>) {

    }

    override fun dialogLoaded(dialog: DialogWrapper) {
        showLoading(false)
        showDialog(dialog)

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
        Toast.makeText(requireContext(), "Сообщение отправлено", Toast.LENGTH_SHORT).show()
    }

    override fun dialogDeleted() {
        showLoading(false)
        Toast.makeText(requireContext(), "Диалог удален", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun photoVideoLoaded() {
        showLoading(false)
        Toast.makeText(requireContext(), "Медиа отпралено", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Запрос на покупку отправлен", Toast.LENGTH_SHORT).show()
                cancelWidgetBinding.cancel.setOnClickListener { cancelBuy(info.suggest!!) }

            }
            MessageActionType.BUY_REQUEST_CANCEL -> {
                Toast.makeText(requireContext(), "Сделка отменена", Toast.LENGTH_SHORT).show()

            }
            MessageActionType.PRICE_SUGGEST -> Toast.makeText(requireContext(), "Предложение цены отправлено", Toast.LENGTH_SHORT).show()
            else -> {}
        }
    }
}