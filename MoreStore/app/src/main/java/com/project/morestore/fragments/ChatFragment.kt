package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
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
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace
import dev.jorik.stub.defToast
import moxy.ktx.moxyPresenter

class ChatFragment : FullscreenMvpFragment(), MenuBottomDialogFragment.Callback, PriceDialog.Callback, ChatMvpView {
    private lateinit var views :FragmentChatBinding
    private val presenter by moxyPresenter { ChatPresenter(requireContext()) }
    private var currentUserId: Long? = null
    private val adapter = MessagesAdapter(
        {
            stubAcceptDealRunnable.run()
            views.bottomBar.visibility = GONE
        },
        { requireContext().defToast(R.string.cancel) },
        { findNavController().navigate(R.id.action_chatFragment_to_mediaFragment) }
    )
    private var listenGeo = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChatBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        with(views){
            toolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
            list.setSpace(24.dp)
            list.adapter = adapter
        }
        when(val chatType = requireArguments().getString(Chat::class.java.simpleName)){
            Chat.Support::class.java.simpleName -> showSupport()
            Chat.Personal::class.java.simpleName -> showSell()
            Chat.Deal::class.java.simpleName -> {
                val userId = requireArguments().getLong(USER_ID_KEY, 0)
                val productId = requireArguments().getLong(PRODUCT_ID_KEY, 0)
                val dialogId = requireArguments().getLong(DIALOG_ID_KEY, 0)
                if(userId != 0L && productId != 0L)
                    createDealChat(userId, productId)
                else getDialog(dialogId)
            }//showDeal()
            else -> throw IllegalArgumentException("Undefined chat type: $chatType")
        }
    }

    private fun showSell(){
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
    }

    private fun showSupport() {
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

    private fun showDeal(dialog: DialogWrapper){
        adapter.avatarUri = dialog.dialog.user.avatar?.photo.orEmpty()
        with(views){
            toolbar.title.text = dialog.dialog.user.name
            toolbar.subtitle.text = "В сети 2 ч. назад"
            toolbar.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_dropdown, 0)
            toolbar.title.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.PROFILE)
                    .show(childFragmentManager, null)
            }
            name.text = dialog.product.name
            toolbar.icon.visibility = GONE
            Glide.with(photo)
                .load(dialog.product.photo.first().photo)
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
        val messages = dialog.messages?.map {
            if(it.idSender == currentUserId)
            Message.My("13:00", R.drawable.ic_check_double, it.text)
            else
                Message.Companion(listOf(Msg("13:10", it.text)))
        }
        adapter.setItems(messages.orEmpty().reversed())
        views.list.scrollToPosition(adapter.itemCount - 1)
    }



    private fun createDealChat(userId: Long, productId: Long){
        presenter.createDialog(userId, productId)

    }

    private fun getDialog(id: Long){
        presenter.getDialogById(id)
    }

    //todo remove stubs
    private val support = listOf(
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
    )

    private val buyer = listOf(
        Message.Divider(R.string.today),
        Message.Companion(listOf(
            Msg("13:16", "Ещё продаёте товар?"),
            Msg("13:19", "Здравствуйте! Хотела спросить, какую скидку можете сделать?")
        )),
        Message.My("13:18", R.drawable.ic_check_double, "Добрый день! Для вас готова сделать скидку"),
        Message.Special.DealRequest("13:19")
    )
    private val accepted = buyer
        .map { if(it is Message.Special.DealRequest) Message.Special.DealAccept("13:19") else it }
        .toMutableList()
        .also { it.add(Message.Special.DealDetails()) }

    private val seller = listOf(
        Message.Divider(R.string.today),
        Message.My("13:18", R.drawable.ic_check_double, "Здравствуйте! Еще продаете? "),
        Message.Companion(listOf(Msg("13:18","Добрый день! Для вас готова сделать скидку")))
    )

    private val seller2 = seller + Message.Special.BuyRequest("13:20", R.drawable.ic_check_single)

    private val seller3 = seller2 + Message.Special.BuyDetails()

    private val seller4 = seller3 + Message.Special.GeoDetails()

    private val stubAcceptDealRunnable :Runnable get() = Runnable { adapter.setItems(accepted) }

    private fun buy(){
        views.bottomBar.removeAllViews()
        WidgetDealCancelBinding.inflate(layoutInflater)
            .also {
                views.bottomBar.addView(it.root)
                it.cancel.setOnClickListener { cancelBuy() }
            }
        adapter.setItems(seller2)
        views.send.setOnClickListener {
            adapter.setItems(seller3)
            listenGeo = true
        }
    }

    private fun cancelBuy(){
        views.bottomBar.removeAllViews()
        adapter.setItems(seller)
        WidgetBuyBarBinding.inflate(layoutInflater)
            .also {
                views.bottomBar.addView(it.root)
                it.myPrice.setOnClickListener {  }
                it.buy.setOnClickListener { buy() }
            }
        views.send.setOnClickListener { /* remove */ }
        listenGeo = false
    }

    private fun setClickListeners(){
        views.send.setOnClickListener {
            if(views.messageEditText.text.isNullOrBlank())
                return@setOnClickListener
            presenter.addMessage(views.messageEditText.text.toString())
            adapter.addMessage(Message.My("13:00", R.drawable.ic_check_double, views.messageEditText.text.toString()))
            views.list.scrollToPosition(adapter.itemCount - 1)
            views.messageEditText.text.clear()

        }
    }

    private fun requestPrice(newPrice: String) = listOf<Message>( *seller.toTypedArray(),
        Message.Special.PriceRequest("13:20", R.drawable.ic_check_double, newPrice),
        Message.Special.PriceAccepted("${getString(R.string.priceDownTo)} $newPrice")
    )

    override fun selectAction(item: MenuBottomDialogFragment.MenuItem) {
        if(!listenGeo) return
        if(item.titleId == R.string.chat_menu_setGeoDeal) adapter.setItems(seller4)
    }

    override fun applyNewPrice(newPrice: String){
        adapter.setItems(requestPrice(newPrice))
    }

    companion object{
        const val USER_ID_KEY = "user_id"
        const val PRODUCT_ID_KEY = "product_id"
        const val DIALOG_ID_KEY = "dialog_id"
    }

    override fun loading() {

    }

    override fun dialogsLoaded(dialogs: List<DialogWrapper>) {

    }

    override fun dialogLoaded(dialog: DialogWrapper) {
        showDeal(dialog)

    }

    override fun dialogCreated(dialogId: CreatedDialogId) {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun currentUserIdLoaded(id: Long) {
        currentUserId = id
    }

    override fun messageSent(message: MessageModel) {
        Toast.makeText(requireContext(), "Сообщение отправлено", Toast.LENGTH_SHORT).show()
    }
}