package com.project.morestore.presentation.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.morestore.databinding.ItemFavoritesellerBinding
import com.project.morestore.data.models.User
import com.project.morestore.util.inflater

class FavoriteSellersAdapter(val onClick: (User) -> Unit) :RecyclerView.Adapter<FavoriteSellersAdapter.FavoriteSellerHolder>() {
    private var items = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteSellerHolder {
        return FavoriteSellerHolder(
            ItemFavoritesellerBinding.inflate(parent.inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavoriteSellerHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setItems(newItems :List<User>){
        items = newItems
        notifyDataSetChanged()
    }

    inner class FavoriteSellerHolder(
        private val views :ItemFavoritesellerBinding
    ) :RecyclerView.ViewHolder(views.root){

        init {
            itemView.setOnClickListener {
                onClick(items[adapterPosition])
            }
        }

        fun bind(seller :User){
            with(views){
                Glide.with(itemView)
                    .load(seller.avatar?.photo)
                    .circleCrop()
                    .into(views.photo)
                name.text = seller.name
                rate.text = seller.rating?.value.toString()
            }
        }
    }
}