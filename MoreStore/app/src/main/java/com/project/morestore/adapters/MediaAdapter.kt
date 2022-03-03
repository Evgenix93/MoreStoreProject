package com.project.morestore.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.R

class MediaAdapter(host :Fragment) :FragmentStateAdapter(host) {
    override fun getItemCount(): Int = 5 //fixme this is stub

    override fun createFragment(position: Int) = MediaFragment()

    class MediaFragment :Fragment(){
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ) = ImageView(requireContext())
            .apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setImageResource(R.drawable.jacket)
            }
    }
}