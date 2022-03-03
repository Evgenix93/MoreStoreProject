package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.project.morestore.R
import com.project.morestore.adapters.ChatsAdapter
import com.project.morestore.databinding.FragmentMessagesBinding
import com.project.morestore.databinding.TabCategoryBinding
import com.project.morestore.fragments.base.BottomNavigationFragment
import com.project.morestore.models.Chat
import com.project.morestore.util.MiddleDivider
import com.project.morestore.util.setSelectListener
import kotlin.reflect.KClass

class MessagesFragment: BottomNavigationFragment() {
    private lateinit var views :FragmentMessagesBinding
    private val adapter = ChatsAdapter{
        if(it.name == "Adidas men's blue denim"){
            findNavController().navigate(R.id.action_messagesFragment_to_chatLotsFragment)
        } else if(it is Chat.Support){
            findNavController().navigate(R.id.action_messagesFragment_to_chatFragment,
                bundleOf(createTypeBundle(Chat.Support::class))
            )
        } else if(it.name == "Сапоги salamander 35"){
            findNavController().navigate(R.id.action_messagesFragment_to_chatFragment,
                bundleOf(createTypeBundle(Chat.Deal::class))
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
        with(views){
            list.adapter = adapter
            list.addItemDecoration(MiddleDivider(requireContext(), R.drawable.div_horline_gray_1))
            tabs.addTab(tabs.newTab().apply { fill(0, "Все", 11) })
            tabs.addTab(tabs.newTab().apply { fill(R.drawable.sel_bag_icon, "Сделки", 2) })
            tabs.addTab(tabs.newTab().apply { fill(R.drawable.set_sticker_icon, "Мои объявления", 2) })
            tabs.setSelectListener {
                adapter.setItems(
                    when(it.position){
                        1 -> stubs.deals
                        2 -> stubs.lots
                        else -> stubs.all
                    }
                )
            }
        }
        adapter.setItems(stubs.all)
    }

    private fun TabLayout.Tab.fill(@DrawableRes drawableId :Int, text :String, count :Int = 0){
        TabCategoryBinding.inflate(this@MessagesFragment.layoutInflater, this.view, false)
            .apply {
                customView = root
                title.text = text
                if(drawableId == 0) icon.visibility = GONE
                else icon.setImageResource(drawableId)

                if(count == 0) this.count.visibility = GONE
                else this.count.text = count.toString()
            }
    }

    private fun createTypeBundle(value :KClass<*>) :Pair<String, String>{
        return Chat::class.java.simpleName to value.java.simpleName
    }

    //todo remove stubs
    private val stubs by lazy { Stubs() }
    inner class Stubs{

        val all = listOf(
            Chat.Personal("Вечернее платье", "Здравствуйте! Еще продаете?",
                R.drawable.avatar1,
                3890f,
                2,
                true
            ),
            Chat.Deal("Пальто", "Алекса Н.",
                R.drawable.avatar2,
                5690f
            ),
            Chat.Support(getString(R.string.chat_support_title), getString(R.string.chat_support_description)),
            Chat.Personal("Плащ трейч", "Здравствуйте! Еще продаете?",
                R.drawable.avatar3,
                8000f,
                totalUnread = 2
            ),
            Chat.Deal("Сапоги salamander 35", "Екатерина О.",
                R.drawable.avatar4,
                3650f
            ),
            Chat.Personal("Толстовка", "Анна К.",
                R.drawable.avatar5,
                2680f,
                online = true
            ),
            Chat.Personal("Кроссовки женские", "Валентина С.",
                R.drawable.avatar6,
                3290f
            ),
            Chat.Personal("Туфли кожаные Keddo ...", "Иван К.",
                R.drawable.avatar7,
                1350f
            ),
            Chat.Personal("Пальто", "Елена Б.",
                R.drawable.avatar2,
                12680f,
                online = true
            ),
            Chat.Lot("Adidas men's blue denim", "5 покупателей",
                R.drawable.avatar8,
                2000f,
                5
            ),
            Chat.Lot("Кроссовки мужские", "2 покупателя",
                R.drawable.avatar9,
                2000f,
                5,
            )
        )
        val deals = all.filterIsInstance<Chat.Deal>()
        val lots = all.filterIsInstance<Chat.Lot>()
    }
}