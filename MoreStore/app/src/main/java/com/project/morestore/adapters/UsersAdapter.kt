package com.project.morestore.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.morestore.databinding.ItemReviewSellerBinding
import com.project.morestore.models.User
import com.project.morestore.util.inflater

class UsersAdapter(private val onClick:(User) -> Unit): RecyclerView.Adapter<UsersAdapter.SellerHolder>() {
    private var sellers = listOf<User>()

    fun updateList(newList: List<User>){
        sellers = newList
        notifyDataSetChanged()
    }
    inner class SellerHolder(private val binding: ItemReviewSellerBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(user: User){
            Glide.with(itemView)
                .load(user.avatar?.photo as String)
                .into(binding.avatarImageView)
            binding.nameTextView.text = user.name
            binding.root.setOnClickListener{onClick(user)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerHolder {
        return SellerHolder(ItemReviewSellerBinding.inflate(parent.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SellerHolder, position: Int) {
         holder.bind(sellers[position])
    }

    override fun getItemCount(): Int {
     return  sellers.size
    }
}