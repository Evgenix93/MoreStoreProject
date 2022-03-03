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
import com.project.morestore.models.Chat
import com.project.morestore.util.MiddleDivider
import kotlin.reflect.KClass

class LotChatsFragment :BottomNavigationFragment() {
    private lateinit var views :FragmentLotchatsBinding
    private val adapter = ChatsAdapter {
        if(it is Chat.Personal && it.name == "Влада Т."){
            findNavController().navigate(R.id.action_chatLotsFragment_to_chatFragment,
                bundleOf(createTypeBundle(Chat.Personal::class))
            )
        }
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
            toolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
            toolbar.title.text = "Adidas men's blue denim"
            toolbar.subtitle.text = getString(R.string.pattern_price, String.format("%,d", 2000))

            list.adapter = adapter
            list.addItemDecoration(MiddleDivider(requireContext(), R.drawable.div_horline_gray_1))
            Glide.with(toolbar.icon)
                .load(R.drawable.avatar8)
                .circleCrop()
                .into(toolbar.icon)
        }
        adapter.setItems(stubs)
    }

    private fun createTypeBundle(value : KClass<*>) :Pair<String, String>{
        return Chat::class.java.simpleName to value.java.simpleName
    }

    //todo delete stubs
    private val stubs = listOf(
        Chat.Personal("Екатерина М.", "Здравствуйте! Еще продаете? ",
            R.drawable.user1,
            0f,
            1,
            true
        ),
        Chat.Personal("Влада Т.", "Здравствуйте! Хотела спросить ...",
            R.drawable.user2,
            0f,
            2
        ),
        Chat.Personal("Богдан В.", "Интересно, какую скидку вы ...",
            R.drawable.user3,
            0f,
            online = true
        ),
        Chat.Personal("Иван И.", "Здравствуйте! Еще продаете?",
            R.drawable.user4,
            0f
        ),
        Chat.Personal("Сергей С.", "Здравствуйте! Интересно ваше ...",
            R.drawable.user5,
            0f
        )
    )
}