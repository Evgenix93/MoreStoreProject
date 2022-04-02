package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.databinding.ScreenEmptylistBinding
import dev.jorik.emptylistgallery.EmptyListGallery

abstract class ListFragment :Fragment() {
    protected abstract val emptyList :EmptyList
    protected abstract val list :RecyclerView
    private var empty :View? = null
    private lateinit var container :FrameLayout
    private val emptyListView :EmptyListGallery by lazy {
        EmptyListGallery(requireContext(), emptyList.img1, emptyList.img2, emptyList.img3)
            .apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 0)
                    .apply { weight = 1f }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.container_empty, container, false)
        .also {
            this.container = it as FrameLayout
            this.container.addView(list)
        }

    protected fun showEmptyList(action :() -> Unit){
        ScreenEmptylistBinding.inflate(layoutInflater, container, false)
            .also {
                it.root.addView(emptyListView, 0)
                container.addView(it.root)
                empty = it.root
                it.text.text = emptyList.message
                it.action.text = emptyList.actionText
                it.action.setOnClickListener { action() }
            }
        list.visibility = View.GONE
    }

    protected fun showList(){
        empty?.let { container.removeView(it) }
        list.visibility = View.VISIBLE
    }

    protected class EmptyList(
        @DrawableRes val img1 :Int,
        @DrawableRes val img2 :Int,
        @DrawableRes val img3 :Int,
        val message :String,
        val actionText :String
    )
}