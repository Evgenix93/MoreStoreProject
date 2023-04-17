package com.project.morestore.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.presentation.fragments.MediaFragmentSmall

class MediaAdapter(host :Fragment, val photos :Array<String>, val onClick: (String) -> Unit) :FragmentStateAdapter(host) {
    override fun getItemCount(): Int = photos.size

    override fun createFragment(position: Int) = MediaFragmentSmall.create(photos[position]) {
        onClick(photos[position])
    }

}