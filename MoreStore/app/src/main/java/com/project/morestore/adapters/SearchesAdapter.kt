package com.project.morestore.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.databinding.ItemSearchBinding
import com.project.morestore.models.Search
import com.project.morestore.util.inflater

class SearchesAdapter(
    private val editCallback :(Search)->Unit
) :RecyclerView.Adapter<SearchesAdapter.SearchHolder>(){
    private var items = listOf<Search>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        return SearchHolder(ItemSearchBinding.inflate(parent.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setItems(newItems :List<Search>){
        items = newItems
        notifyDataSetChanged()
    }

    inner class SearchHolder(
        private val views :ItemSearchBinding
    ) :RecyclerView.ViewHolder(views.root){
        private val context = views.root.context
        private lateinit var search :Search
        init{
            views.edit.setOnClickListener { editCallback(search) }
        }

        fun bind(search :Search){
            this.search = search
            with(views){
                title.text = search.title
                filters.text = search.filters.joinToString(context.getString(R.string.dot_separator))
                notifications.text = context.getString(when(search.notification){
                    Search.Notification.DAYLY -> R.string.favorite_searchNotify_dayly
                    Search.Notification.DISABLE -> R.string.favorite_searchNotify_disable
                    Search.Notification.WEEKLY -> R.string.favorite_searchNotify_weekly
                })
            }
        }
    }
}