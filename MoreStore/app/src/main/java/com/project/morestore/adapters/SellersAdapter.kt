package com.project.morestore.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.morestore.databinding.ItemReviewSellerBinding
import com.project.morestore.models.User
import com.project.morestore.util.inflater

class SellersAdapter: RecyclerView.Adapter<SellersAdapter.SellerHolder>() {
    private var sellers = listOf<User>()

    inner class SellerHolder(private val binding: ItemReviewSellerBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(user: User){
            Glide.with(itemView)
                .load(user.avatar?.photo as String)
                .into(binding.avatarImageView)
            binding.nameTextView.text = user.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerHolder {
        return SellerHolder(ItemReviewSellerBinding.inflate(parent.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SellerHolder, position: Int) {

    }

    override fun getItemCount(): Int {
     return  sellers.size
    }
}