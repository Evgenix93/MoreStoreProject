package com.project.morestore.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.project.morestore.R

class SuggestionArrayAdapter(contextRef: Context, val res: Int, private val array: List<String>, val onClick: (Int, String) -> Unit): ArrayAdapter<String>(contextRef, res, array) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return if(position == 0){
            LayoutInflater.from(context).inflate(R.layout.item_suggestion_textview2, parent, false).apply {
                (rootView as TextView).text = getItem(position)
                setOnClickListener { onClick(position, getItem(position).toString()) }
            }
        }else{
            LayoutInflater.from(context).inflate(R.layout.item_suggestion_textview, parent, false).apply {
                (rootView as TextView).text = getItem(position)
                setOnClickListener { onClick(position, getItem(position).toString()) }
            }
        }
    }
}