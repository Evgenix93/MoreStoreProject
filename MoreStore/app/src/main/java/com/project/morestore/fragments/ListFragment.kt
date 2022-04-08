package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.project.morestore.R
import com.project.morestore.databinding.ScreenEmptylistBinding
import com.project.morestore.models.ProductBrand
import dev.jorik.emptylistgallery.EmptyListGallery
import moxy.MvpAppCompatFragment

abstract class ListFragment :MvpAppCompatFragment() {
    protected abstract val emptyList :EmptyList
    protected abstract val list :RecyclerView
    protected val loader: ProgressBar by lazy {requireView().findViewById(R.id.loadingProgressBar)}
    private var empty :View? = null
    private lateinit var container :LinearLayout
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
            this.container = it.findViewById(R.id.containerLinearLayout) as LinearLayout
            if(list.parent == null)
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
        requireView().findViewById<ConstraintLayout>(R.id.brandsConstraintLayout).isVisible = false
    }

    protected fun showList(){
        empty?.let { container.removeView(it) }
        list.visibility = View.VISIBLE
    }

    protected fun showBtn(show: Boolean){
        requireView().findViewById<MaterialButton>(R.id.addSearchBtn).isVisible = show
    }

    protected fun showBrandsView(brands: List<ProductBrand>){
       val brandsConstraintLayout = requireView().findViewById<ConstraintLayout>(R.id.brandsConstraintLayout)
       brandsConstraintLayout.isVisible = true
       requireView().findViewById<View>(R.id.brandClickView).setOnClickListener {
           findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToBrandsFragment(brands.toTypedArray()))
       }
       val brandsTextView = requireView().findViewById<TextView>(R.id.brandsTextView)
       brandsTextView.text = brands.joinToString(", ") { it.name }
    }

    protected class EmptyList(
        @DrawableRes val img1 :Int,
        @DrawableRes val img2 :Int,
        @DrawableRes val img3 :Int,
        val message :String,
        val actionText :String
    )
}