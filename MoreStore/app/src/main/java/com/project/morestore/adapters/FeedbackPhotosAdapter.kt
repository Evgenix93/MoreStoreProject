package com.project.morestore.adapters

import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.morestore.databinding.ItemPhotoAddBinding
import com.project.morestore.databinding.ItemPhotoBinding
import com.project.morestore.databinding.ItemPhotoDescriptionBinding
import com.project.morestore.models.FeedbackItem
import com.project.morestore.util.inflater

class FeedbackPhotosAdapter(
    private val callback :(FeedbackItem) -> Unit
) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = listOf<FeedbackItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = parent.inflater
        return when(viewType){
            Type.ADD.ordinal -> AddViewHolder(ItemPhotoAddBinding.inflate(inflater, parent, false))
            Type.DESC.ordinal -> DescViewHolder(ItemPhotoDescriptionBinding.inflate(inflater, parent, false))
            Type.PHOTO.ordinal -> PhotoViewHolder(ItemPhotoBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("no defined type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when(getItemViewType(position)){
            Type.ADD.ordinal -> (holder as AddViewHolder).bind(item as FeedbackItem.AddPhoto)
            Type.DESC.ordinal -> (holder as DescViewHolder).bind(item as FeedbackItem.Description)
            Type.PHOTO.ordinal -> (holder as PhotoViewHolder).bind(item as FeedbackItem.Photo)
        }
    }

    override fun getItemCount() = items.size

    fun setItems(newItems :List<FeedbackItem>){
        items = newItems
        notifyDataSetChanged()
    }

    fun getItems(): List<FeedbackItem> = items

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){
            is FeedbackItem.AddPhoto -> Type.ADD.ordinal
            is FeedbackItem.Description -> Type.DESC.ordinal
            is FeedbackItem.Photo -> Type.PHOTO.ordinal
        }
    }

    private enum class Type { ADD, DESC, PHOTO }

    inner class AddViewHolder(
        private val views :ItemPhotoAddBinding
    ) :RecyclerView.ViewHolder(views.root){
        init {
            views.root.setOnClickListener{ callback(FeedbackItem.AddPhoto) }
        }
        fun bind(app :FeedbackItem.AddPhoto){
            //todo implement
        }
    }

    inner class DescViewHolder(
        private val views :ItemPhotoDescriptionBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(desc :FeedbackItem.Description){
            //todo implement
        }
    }

    inner class PhotoViewHolder(
        private val views :ItemPhotoBinding
    ) :RecyclerView.ViewHolder(views.root){
        private lateinit var photo :Uri
        init {
            views.photo.clipToOutline = true
        }

        fun bind(photo :FeedbackItem.Photo){
            this.photo = photo.photo
           // views.photo.setImageURI(this.photo)
            Glide.with(views.root)
                .load(photo.photo)
                .into(views.photo)
        }
    }
}