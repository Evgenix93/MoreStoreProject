package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
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
import com.project.morestore.fragments.base.FullscreenFragment
import com.project.morestore.models.Chat
import com.project.morestore.models.Media
import com.project.morestore.models.Message
import com.project.morestore.models.Msg
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace
import dev.jorik.stub.defToast

class ChatFragment :FullscreenFragment(), MenuBottomDialogFragment.Callback, PriceDialog.Callback {
    private lateinit var views :FragmentChatBinding
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
        with(views){
            toolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
            list.setSpace(24.dp)
            list.adapter = adapter
        }
        when(val chatType = requireArguments().getString(Chat::class.java.simpleName)){
            Chat.Support::class.java.simpleName -> showSupport()
            Chat.Personal::class.java.simpleName -> showSell()
            Chat.Deal::class.java.simpleName -> showDeal()
            else -> throw IllegalArgumentException("Undefined chat type: $chatType")
        }
    }

    private fun showSell(){
        adapter.avatarId = R.drawable.user2
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
            addMedia.setOnClickListener {
                MenuBottomDialogFragment(MenuBottomDialogFragment.Type.GEO)
                    .show(childFragmentManager, null)
            }
        }
        adapter.setItems(buyer)
    }

    private fun showSupport() {
        adapter.avatarId = R.drawable.ic_headphones
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

    private fun showDeal(){
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
}