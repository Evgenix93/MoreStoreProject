package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.adapters.ChatsAdapter
import com.project.morestore.databinding.FragmentLotchatsBinding
import com.project.morestore.fragments.base.BottomNavigationFragment
import com.project.morestore.fragments.base.BottomNavigationMvpFragment
import com.project.morestore.models.Chat
import com.project.morestore.models.CreatedDialogId
import com.project.morestore.models.DialogWrapper
import com.project.morestore.models.MessageModel
import com.project.morestore.mvpviews.ChatMvpView
import com.project.morestore.presenters.ChatPresenter
import com.project.morestore.util.MiddleDivider
import moxy.ktx.moxyPresenter
import kotlin.reflect.KClass

class LotChatsFragment : BottomNavigationMvpFragment(), ChatMvpView {
    private lateinit var views :FragmentLotchatsBinding
    private val presenter by moxyPresenter { ChatPresenter(requireContext()) }
    private val adapter = ChatsAdapter {
        if(it is Chat.Personal && it.name == "Влада Т."){
            findNavController().navigate(R.id.action_chatLotsFragment_to_chatFragment,
                bundleOf(createTypeBundle(Chat.Personal::class))
            )
        }
        /*else{
            findNavController().navigate(
                R.id.chatFragment,
                bundleOf(
                    ChatFragment.DIALOG_ID_KEY to it.id,
                    Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                )

            )
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLotchatsBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        //adapter.setItems(stubs)
    }

    private fun createTypeBundle(value : KClass<*>) :Pair<String, String>{
        return Chat::class.java.simpleName to value.java.simpleName
    }

    //todo delete stubs
   /* private val stubs = listOf(
        Chat.Personal(0,"Екатерина М.", "Здравствуйте! Еще продаете? ",
            R.drawable.user1,
            0f,
            1,
            true
        ),
        Chat.Personal(0,"Влада Т.", "Здравствуйте! Хотела спросить ...",
            R.drawable.user2,
            0f,
            2
        ),
        Chat.Personal(0,"Богдан В.", "Интересно, какую скидку вы ...",
            R.drawable.user3,
            0f,
            online = true
        ),
        Chat.Personal(0,"Иван И.", "Здравствуйте! Еще продаете?",
            R.drawable.user4,
            0f
        ),
        Chat.Personal(0,"Сергей С.", "Здравствуйте! Интересно ваше ...",
            R.drawable.user5,
            0f
        )
    )*/


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
}