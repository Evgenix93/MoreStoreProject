package com.project.morestore.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
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
import com.project.morestore.util.MiddleDivider
import com.project.morestore.util.setSelectListener
import moxy.ktx.moxyPresenter
import kotlin.reflect.KClass

class MessagesFragment : BottomNavigationMvpFragment(), ChatMvpView {
    private lateinit var views: FragmentMessagesBinding
    private val presenter by moxyPresenter { ChatPresenter(requireContext()) }
    private var currentUserId: Long? = null
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMessagesBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkToken()
        with(views) {
            list.adapter = adapter
            list.addItemDecoration(MiddleDivider(requireContext(), R.drawable.div_horline_gray_1))
            tabs.addTab(tabs.newTab().apply { fill(0, "Все", 0) })
            tabs.addTab(tabs.newTab().apply { fill(R.drawable.sel_bag_icon, "Сделки", 0) })
            tabs.addTab(
                tabs.newTab().apply { fill(R.drawable.set_sticker_icon, "Мои объявления", 0) })
            tabs.setSelectListener {
                //adapter.setItems(
                    when (it.position) {
                        1 -> presenter.showDealDialogs() //stubs.deals
                        2 -> presenter.showLotDialogs()//stubs.lots
                        else -> presenter.showAllDialogs()//stubs.all
                    }
                //)
            }
        }

        //adapter.setItems(stubs.all)
        presenter.showAllDialogs()


    }

    private fun TabLayout.Tab.fill(@DrawableRes drawableId: Int, text: String, count: Int = 0) {
        TabCategoryBinding.inflate(this@MessagesFragment.layoutInflater, this.view, false)
            .apply {
                customView = root
                title.text = text
                if (drawableId == 0) icon.visibility = GONE
                else icon.setImageResource(drawableId)

                //if (count == 0) this.count.visibility = GONE
                //else this.count.text = count.toString()
            }
    }

    private fun createTypeBundle(value: KClass<*>): Pair<String, String> {
        return Chat::class.java.simpleName to value.java.simpleName
    }

    private fun checkToken(){
        if(Token.token.isEmpty())
            findNavController().navigate(R.id.cabinetGuestFragment)
    }

    //todo remove stubs
   // private val stubs by lazy { Stubs() }

    /*inner class Stubs {

        val all = listOf(
            Chat.Personal(
                0, "Вечернее платье", "Здравствуйте! Еще продаете?",
                R.drawable.avatar1,
                3890f,
                2,
                true
            ),
            Chat.Deal(
                0, "Пальто", "Алекса Н.",
                R.drawable.avatar2,
                5690f
            ),
            Chat.Support(
                0,
                getString(R.string.chat_support_title),
                getString(R.string.chat_support_description)
            ),
            Chat.Personal(
                0, "Плащ трейч", "Здравствуйте! Еще продаете?",
                R.drawable.avatar3,
                8000f,
                totalUnread = 2
            ),
            Chat.Deal(
                0, "Сапоги salamander 35", "Екатерина О.",
                R.drawable.avatar4,
                3650f
            ),
            Chat.Personal(
                0, "Толстовка", "Анна К.",
                R.drawable.avatar5,
                2680f,
                online = true
            ),
            Chat.Personal(
                0, "Кроссовки женские", "Валентина С.",
                R.drawable.avatar6,
                3290f
            ),
            Chat.Personal(
                0, "Туфли кожаные Keddo ...", "Иван К.",
                R.drawable.avatar7,
                1350f
            ),
            Chat.Personal(
                0, "Пальто", "Елена Б.",
                R.drawable.avatar2,
                12680f,
                online = true
            ),
            Chat.Lot(
                0, "Adidas men's blue denim", "5 покупателей",
                R.drawable.avatar8,
                2000f,
                5
            ),
            Chat.Lot(
                0, "Кроссовки мужские", "2 покупателя",
                R.drawable.avatar9,
                2000f,
                5,
            )
        )
        val deals = all.filterIsInstance<Chat.Deal>()
        val lots = all.filterIsInstance<Chat.Lot>()
    }*/

    override fun loading() {

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

}