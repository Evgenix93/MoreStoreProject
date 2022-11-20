package com.project.morestore.adapters

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.databinding.ItemSearchBinding
import com.project.morestore.data.models.FavoriteSearch
import com.project.morestore.util.inflater

class SearchesAdapter(
    private val editCallback :(FavoriteSearch)->Unit
) :RecyclerView.Adapter<SearchesAdapter.SearchHolder>(){
    private var items = listOf<FavoriteSearch>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        return SearchHolder(ItemSearchBinding.inflate(parent.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setItems(newItems :List<FavoriteSearch>){
        items = newItems
        notifyDataSetChanged()
    }

    inner class SearchHolder(
        private val views :ItemSearchBinding
    ) :RecyclerView.ViewHolder(views.root){
        private val context = views.root.context
        private lateinit var search :FavoriteSearch
        init{
            views.root.setOnClickListener { editCallback(search) }
        }

        fun bind(search :FavoriteSearch){
            this.search = search
            with(views){
                title.text = search.value.name
                val forWho = when(search.value.chosenForWho.indexOf(true)){
                    0 -> listOf("Женщинам")
                    1 -> listOf("Мужчинам")
                    2 -> listOf("Детям")
                    else -> listOf("")

                }

                val categoryStr = search.value.categories.filter { it.isChecked == true }.map { it.name }
                val brand = search.value.brands.filter { it.isChecked == true }.map { it.name }
                val condition = search.value.chosenConditions.mapIndexed() { index, chosen ->
                    if (chosen)
                        when (index) {
                            0 -> "Новое с биркой"
                            1 -> "Новое без бирок"
                            2 -> "Отличное"
                            3 -> "Хорошее"
                            else -> ""
                        }
                    else ""
                }.filter { it.isNotEmpty() }

                    val sizes = search.value.chosenTopSizesWomen.filter { it.isSelected }.map { it.int }
                    val sizesBottom = search.value.chosenBottomSizesWomen.filter { it.isSelected }.map { it.int }
                    val sizesShoos = search.value.chosenShoosSizesWomen.filter { it.isSelected }.map { it.int }
                    val sizesTopMen = search.value.chosenTopSizesMen.filter { it.isSelected }.map { it.int }
                    val sizesBottomMen = search.value.chosenBottomSizesMen.filter { it.isSelected }.map { it.int }
                    val sizesShoosMen = search.value.chosenShoosSizesMen.filter { it.isSelected }.map { it.int }
                    val sizesTopKids = search.value.chosenTopSizesKids.filter { it.isSelected }.map { it.int }
                    val sizesBottomKids = search.value.chosenBottomSizesKids.filter { it.isSelected }.map { it.int }
                    val sizesShoosKids = search.value.chosenShoosSizesKids.filter { it.isSelected }.map { it.int }
                val colors = search.value.colors.filter { it.isChecked == true }.map { it.name }
                val materials = search.value.chosenMaterials.filter { it.isSelected }.map { it.name }
                val styles = search.value.chosenStyles.filter { it.isChecked == true }.map { it.name }


                val str= forWho + categoryStr + brand + condition + sizes + sizesBottom + sizesShoos + sizesTopMen + sizesBottomMen + sizesShoosMen + sizesTopKids + sizesBottomKids + sizesShoosKids + colors + materials + styles
                views.filters.text = str.joinToString( context.getString(R.string.dot_separator))

                views.notifications.text = search.value.notificationType

                Log.d("mylog", str.joinToString(","))
                Log.d("mylog", search.value.chosenConditions.toString())


















                }
                //filters.text = search.propertyValues?.joinToString(context.getString(R.string.dot_separator))
                /*notifications.text = context.getString(when(search.notification){
                    Search.Notification.DAYLY -> R.string.favorite_searchNotify_dayly
                    Search.Notification.DISABLE -> R.string.favorite_searchNotify_disable
                    Search.Notification.WEEKLY -> R.string.favorite_searchNotify_weekly
                })*/
            }
        }
    }
