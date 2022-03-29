package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.project.morestore.R
import com.project.morestore.adapters.PriceViewPagerAdapter
import com.project.morestore.databinding.FragmentRaiseProductBinding
import com.project.morestore.util.autoCleared

class RaiseProductFragment: Fragment(R.layout.fragment_raise_product) {
    private val binding: FragmentRaiseProductBinding by viewBinding()
    private var pricesViewPagerAdapter: PriceViewPagerAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initPricesViewPager()
    }

    private fun initPricesViewPager(){
        pricesViewPagerAdapter = PriceViewPagerAdapter(this)
        binding.pricesViewPager.adapter = pricesViewPagerAdapter
        TabLayoutMediator(binding.daysTabLayout, binding.pricesViewPager){tab,position ->
            when (position){
                0 -> {
                    tab.text = getString(R.string.for1_day)
                }
                1 -> {
                    tab.text = getString(R.string.for7_days)
                }
            }}.attach()

        binding.pricesViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if(position == 0)
                binding.backgroundView.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.raise_product_background
                )
                else
                    binding.backgroundView.background = AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.raise_product_background2
                    )
            }
        } )
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Получите больше просмотров"
        binding.toolbar.backIcon.setOnClickListener{
            findNavController().popBackStack()
        }
    }

}