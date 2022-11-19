package com.project.morestore.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.adapters.ChatsAdapter
import com.project.morestore.databinding.FragmentLotchatsBinding
import com.project.morestore.fragments.base.BottomNavigationFragment

import com.project.morestore.models.*
import com.project.morestore.mvpviews.ChatMvpView
import com.project.morestore.presenters.ChatPresenter
import com.project.morestore.util.MessageActionType
import com.project.morestore.util.MessagingService
import com.project.morestore.util.MiddleDivider
import moxy.ktx.moxyPresenter


class LotChatsFragment : BottomNavigationFragment(), ChatMvpView {
    private lateinit var views :FragmentLotchatsBinding
    private val presenter by moxyPresenter { ChatPresenter(requireContext()) }
    private val adapter = ChatsAdapter {
        findNavController().navigate(
                R.id.chatFragment,
                bundleOf(
                    ChatFragment.DIALOG_ID_KEY to it.id,
                    Chat::class.java.simpleName to Chat.Personal::class.java.simpleName
                )

            )

    }
    private val messageReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            val dialogId = intent?.getLongExtra(MessagingService.MESSAGE_KEY, -1L)
            dialogId?.let {
                val productId = arguments?.getLong(PRODUCT_ID_KEY, -1)
                presenter.handlePushMessageLotsChatFragment(it, productId ?: -1)

            }


        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLotchatsBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerReceiver()
        with(views){
            //todo create toolbar widget
            val productId = arguments?.getLong(PRODUCT_ID_KEY, -1)
            val productName = arguments?.getString(PRODUCT_NAME, "")
            val productPrice = arguments?.getFloat(PRODUCT_PRICE_KEY, 0f)
            val productImage = arguments?.getString(PRODUCT_IMAGE_KEY, "")
            if(productId != -1L){
                presenter.showProductDialogs(productId!!)
            }


            toolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
            toolbar.title.text = productName
            toolbar.subtitle.text = "$productPrice ₽" //getString(R.string.pattern_price, String.format("%,d", 2000))

            list.adapter = adapter
            list.addItemDecoration(MiddleDivider(requireContext(), R.drawable.div_horline_gray_1))
            Glide.with(toolbar.icon)
                .load(productImage)
                .circleCrop()
                .into(toolbar.icon)
        }

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




    companion object{
        const val PRODUCT_ID_KEY = "product_id"
        const val PRODUCT_NAME = "product_name"
        const val PRODUCT_PRICE_KEY = "product_price"
        const val PRODUCT_IMAGE_KEY = "product_image"
    }

    override fun loading() {

    }

    override fun dialogsLoaded(dialogs: List<Chat>) {
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

    }

    override fun mediaUrisLoaded(mediaUris: List<Uri>?) {
        TODO("Not yet implemented")
    }

    override fun actionMessageSent(info: ChatFunctionInfo, type: MessageActionType) {

    }

    override fun showUnreadMessagesStatus(show: Boolean) {

    }

    override fun showUnreadTab(tab: Int, unread: Boolean) {

    }

    override fun productAddedToCart(product: Product, cartId: Long) {

    }

}