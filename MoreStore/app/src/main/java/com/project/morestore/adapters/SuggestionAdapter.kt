package com.project.morestore.adapters

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ListAdapter
import com.project.morestore.R

class SuggestionAdapter(val context: Context, val res: Int, val list: List<String>): ListAdapter, Filterable {
    override fun registerDataSetObserver(p0: DataSetObserver?) {

    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getCount(): Int {
        return 3

    }

    override fun getItem(p0: Int): Any {
        return Any()

    }

    override fun getItemId(p0: Int): Long {
        return 1
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.item_suggestion, p2, false)

    }

    override fun getItemViewType(p0: Int): Int {
        return 1
    }

    override fun getViewTypeCount(): Int {
        return 0
    }

    override fun isEmpty(): Boolean {
        return false
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun isEnabled(p0: Int): Boolean {
        return true
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                return FilterResults()
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {

            }

        }
    }


}