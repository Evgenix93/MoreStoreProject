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
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.project.morestore.R
import com.project.morestore.adapters.ChatsAdapter
import com.project.morestore.databinding.FragmentMessagesBinding
import com.project.morestore.databinding.TabCategoryBinding
import com.project.morestore.fragments.base.BottomNavigationFragment
import com.project.morestore.fragments.base.BottomNavigationMvpFragment
import com.project.morestore.models.*
import com.project.morestore.mvpviews.ChatMvpView
import com.project.morestore.presenters.ChatPresenter
import com.project.morestore.singletones.Token
import com.project.morestore.util.MessageActionType
import com.project.morestore.util.MessagingService
import com.project.morestore.util.MiddleDivider
import com.project.morestore.util.setSelectListener
import moxy.ktx.moxyPresenter
import kotlin.reflect.KClass

class MessagesFragment : BottomNavigationMvpFragment(), ChatMvpView {
    private lateinit var views: FragmentMessagesBinding
    private val presenter by moxyPresenter { ChatPresenter(requireContext()) }
    private val args: MessagesFragmentArgs by navArgs()
    private val adapter = ChatsAdapter {
        if (it.name == "Adidas men's blue denim") {
            findNavController().navigate(R.id.action_messagesFragment_to_chatLotsFragment)
        } else if (it is Chat.Support) {
            findNavController().navigate(
                R.id.action_messagesFragment_to_chatFragment,
                bundleOf(createTypeBundle(Chat.Support::class),
                ChatFragment.DIALOG_ID_KEY to it.id)
            )
        } else if (it.name == "Сапоги salamander 35") {
            findNavController().navigate(
                R.id.action_messagesFragment_to_chatFragment,
                bundleOf(createTypeBundle(Chat.Deal::class))
            )
        } else if(it is Chat.Deal) {
            findNavController().navigate(
                R.id.action_messagesFragment_to_chatFragment,
                bundleOf(
                    ChatFragment.DIALOG_ID_KEY to it.id,
                    Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                )
            )
        }
        else{
            findNavController().navigate(
                R.id.chatLotsFragment,
                bundleOf(
                    LotChatsFragment.PRODUCT_ID_KEY to (it as Chat.Lot).productId,
                    LotChatsFragment.PRODUCT_NAME to it.name,
                    LotChatsFragment.PRODUCT_PRICE_KEY to it.price,
                    LotChatsFragment.PRODUCT_IMAGE_KEY to it.avatar
                )
            )
        }
    }
    private val messageReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            val dialogId = intent?.getLongExtra(MessagingService.MESSAGE_KEY, -1)
            dialogId ?: return
            when(views.tabs.selectedTabPosition){
                1 -> presenter.handlePushMessageMessagesFragment(dialogId, Chat.Deal::class.simpleName.orEmpty())
                2 -> presenter.handlePushMessageMessagesFragment(dialogId, Chat.Lot::class.simpleName.orEmpty())
                else -> presenter.handlePushMessageMessagesFragment(dialogId, Chat::class.simpleName.orEmpty())
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMessagesBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerReceiver()
        checkToken()
        with(views) {
            list.adapter = adapter
            list.addItemDecoration(MiddleDivider(requireContext(), R.drawable.div_horline_gray_1))
            tabs.addTab(tabs.newTab().apply { fill(0, "Все") })
            tabs.addTab(tabs.newTab().apply { fill(R.drawable.sel_bag_icon, "Сделки") })
            tabs.addTab(
                tabs.newTab().apply { fill(R.drawable.set_sticker_icon, "Мои объявления") })
            tabs.setSelectListener {
                    when (it.position) {
                        1 ->  presenter.showDealDialogs(args.userId)
                        2 -> presenter.showLotDialogs(args.userId)
                        else -> presenter.showAllDialogs(args.userId)
                    }

            }
            tabs.selectTab(tabs.getTabAt(presenter.getTabPosition()))
        }
        presenter.showDialogs(args.userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterReceiver()
    }

    private fun registerReceiver(){
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(messageReceiver,
        IntentFilter(MessagingService.MESSAGE_INTENT)
        )
    }

    private fun unregisterReceiver(){
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageReceiver)
    }

    private fun TabLayout.Tab.fill(@DrawableRes drawableId: Int, text: String) {
        TabCategoryBinding.inflate(this@MessagesFragment.layoutInflater, this.view, false)
            .apply {
                customView = root
                title.text = text
                if (drawableId == 0) icon.visibility = GONE
                else icon.setImageResource(drawableId)
            }
    }

    private fun createTypeBundle(value: KClass<*>): Pair<String, String> {
        return Chat::class.java.simpleName to value.java.simpleName
    }

    private fun checkToken(){
        if(Token.token.isEmpty()) {
            val navOptions =  NavOptions.Builder().setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false).build()
            findNavController().navigate(R.id.cabinetGuestFragment, bundleOf(CabinetGuestFragment.FRAGMENT_ID to R.id.messagesFragment), navOptions)
        }
    }


    override fun loading() {
        views.loader.isVisible = true

    }

    override fun dialogsLoaded(dialogs: List<Chat>) {
        /*val dialogsToShow = dialogs.map {

            val price = it.product.priceNew ?: 0f

            Chat.Personal(
                it.dialog.id,
                it.product.name,
                it.dialog.lastMessage?.text.orEmpty(),
                R.drawable.avatar2,
                price
            )
        }*/
        views.loader.isVisible = false
        adapter.setItems(dialogs)
    }

    override fun dialogLoaded(dialog: DialogWrapper) {

    }

    override fun dialogCreated(dialogId: CreatedDialogId) {

    }

    override fun error(message: String) {

    }

    override fun currentUserIdLoaded(id: Long) {

    }

    override fun messageSent(message: MessageModel) {

    }

    override fun dialogDeleted() {

    }

    override fun photoVideoLoaded() {

    }

    override fun showDialogCount(type: String, count: Int) {
        Log.d("tab", count.toString())
        when(type){
            Chat::class.java.simpleName -> views.tabs.getTabAt(0)?.view?.findViewById<TextView>(R.id.count)?.text = count.toString()//?.fill(0, "Все", count)
            Chat.Deal::class.java.simpleName -> views.tabs.getTabAt(1)?.view?.findViewById<TextView>(R.id.count)?.text = count.toString()//?.fill(0, "Все", count)
            Chat.Lot::class.java.simpleName -> views.tabs.getTabAt(2)?.view?.findViewById<TextView>(R.id.count)?.text = count.toString()//?.fill(0, "Все", count)



        }
    }

    override fun mediaUrisLoaded(mediaUris: List<Uri>?) {
        TODO("Not yet implemented")
    }

    override fun actionMessageSent(info: ChatFunctionInfo, type: MessageActionType) {

    }

    override fun showUnreadMessagesStatus(show: Boolean) {

    }

    override fun showUnreadTab(tab: Int, unread: Boolean) {
        when(tab){
            0 -> views.tabs.getTabAt(0)?.view?.findViewById<ImageView>(R.id.unreadIcon)?.isVisible = unread//?.fill(0, "Все", count)
            1 -> views.tabs.getTabAt(1)?.view?.findViewById<ImageView>(R.id.unreadIcon)?.isVisible = unread//?.fill(0, "Все", count)
            2 -> views.tabs.getTabAt(2)?.view?.findViewById<ImageView>(R.id.unreadIcon)?.isVisible = unread//?.fill(0, "Все", count)



        }

    }

    override fun productAddedToCart(product: Product, cartId: Long) {

    }

}