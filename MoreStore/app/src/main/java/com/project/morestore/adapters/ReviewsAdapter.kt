package com.project.morestore.adapters

import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemReviewAddBinding
import com.project.morestore.databinding.ItemReviewBinding
import com.project.morestore.models.PreviewPhoto
import com.project.morestore.models.Review
import com.project.morestore.models.ReviewItem
import com.project.morestore.models.ReviewListItem
import com.project.morestore.util.createRect
import com.project.morestore.util.diffInDays
import com.project.morestore.util.dp
import com.project.morestore.util.inflater
import java.util.*

class ReviewsAdapter(
    private val create :()->Unit,
    private val viewPhotos :(Review) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = listOf<ReviewListItem>()
    private val now = Calendar.getInstance()//todo refactor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ADD){
            CreateReviewViewHolder(ItemReviewAddBinding.inflate(parent.inflater, parent, false))
        } else {//REVIEW
            ReviewViewHolder(ItemReviewBinding.inflate(parent.inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == ADD){
            //skip
        } else {//REVIEW
            (holder as ReviewViewHolder).bind((items[position-1] as ReviewItem).review)
        }
    }

    override fun getItemCount() = items.size + 1

    override fun getItemViewType(position: Int) = if(position == 0) ADD else REVIEW

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if(holder is ReviewViewHolder) holder.clear()
    }

    fun setItems(newItems :List<ReviewListItem>){
        items = newItems
        notifyDataSetChanged()
    }

    companion object{
        const val ADD = 0
        const val REVIEW = 1
    }

    inner class ReviewViewHolder(
        private val views :ItemReviewBinding
    ) :RecyclerView.ViewHolder(views.root){
        private val context = views.avatar.context
        lateinit var review :Review

        init{
            views.photos.dividerDrawable = createRect(8.dp, 0)
        }

        fun bind(review :Review){
            Log.d("review date", review.date.timeInMillis.toString())
            this.review = review
            with(views){
                Glide.with(views.avatar)
                    .load(review.user.avatar?.photo ?: "")
                    .circleCrop()
                    .into(views.avatar)
                name.text = review.user.name
                before.text = beforeDiff
                rating.rate = review.rate.toInt()
            }
            views.review.text = review.text
            if(review.photo == null){
                views.photos.visibility = GONE
            } else {
                views.photos.removeAllViews()
                for (photo in review.photo){
                    PreviewPhoto(views.avatar.context, photo.photo)//todo simplify
                        .also {
                            it.setOnClickListener { viewPhotos(review) }
                            views.photos.addView(it)
                        }
                }
                views.photos.visibility = VISIBLE
            }
        }

        fun clear() = Glide.with(views.avatar).clear(views.avatar)

        private val beforeDiff :String get() {
            val diff = now.diffInDays(review.date)
            return when {
                diff == 0 -> context.getString(R.string.today)
                diff < 30 -> context.getString(R.string.pattern_before_days, diff)
                diff/365 > 1 -> context.getString(R.string.pattern_before_years, diff/365)
                else -> context.getString(R.string.pattern_before_monthes, diff/30)
            }
        }
    }

    inner class CreateReviewViewHolder(
        private val views :ItemReviewAddBinding
    ) :RecyclerView.ViewHolder(views.root){
        init { itemView.setOnClickListener { create() } }
    }

}