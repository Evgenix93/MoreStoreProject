package com.project.morestore.adapters

import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.databinding.ItemChatBinding
import com.project.morestore.models.Chat
import com.project.morestore.util.inflater

class ChatsAdapter(val callback :(Chat) -> Unit) :RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {
    private val items = mutableListOf<Chat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = ItemChatBinding.inflate(parent.inflater, parent, false)
            .apply { avatar.clipToOutline = true }
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = items[position]
        holder.bind(chat)
        with(holder.views){
            when (chat){
                is Chat.Support -> {
                    avatar.setImageResource(R.drawable.ic_headphones)
                    icon.visibility = GONE
                    price.visibility = GONE
                    online.visibility = GONE
                    unread.visibility = GONE
                }
                is Chat.Personal -> {
                    avatar.setImageResource(chat.avatar)
                    icon.visibility = GONE
                    if(chat.price == 0f){
                        price.visibility = INVISIBLE
                    } else {
                        price.text = root.context.getString(R.string.pattern_price, String.format("%,d", chat.price.toInt()))
                        price.visibility = VISIBLE
                    }
                    online.visibility = if(chat.online) VISIBLE else GONE
                    if(chat.totalUnread == 0){
                        unread.visibility = GONE
                    } else {
                        unread.visibility = VISIBLE
                        unread.text = chat.totalUnread.toString()
                    }
                }
                is Chat.Deal -> {
                    avatar.setImageResource(chat.avatar)
                    icon.setImageResource(R.drawable.ic_bag_filled)
                    icon.visibility = VISIBLE
                    price.text = root.context.getString(R.string.pattern_price, String.format("%,d", chat.price.toInt()))
                    online.visibility = GONE
                    unread.visibility = GONE
                }
                is Chat.Lot -> {
                    avatar.setImageResource(chat.avatar)
                    icon.setImageResource(R.drawable.ic_sticker_filled)
                    icon.visibility = VISIBLE
                    price.text = root.context.getString(R.string.pattern_price, String.format("%,d", chat.price.toInt()))
                    online.visibility = GONE
                    if(chat.totalUnread == 0){
                        unread.visibility = GONE
                    } else {
                        unread.visibility = VISIBLE
                        unread.text = chat.totalUnread.toString()
                    }
                }
            }
            name.text = chat.name
            description.text = chat.description
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems :List<Chat>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(val views :ItemChatBinding) :RecyclerView.ViewHolder(views.root){
        private var chat :Chat? = null

        init {
            itemView.setOnClickListener { callback(chat!!) }
        }

        fun bind(chat :Chat){
            this.chat = chat
        }
    }
}