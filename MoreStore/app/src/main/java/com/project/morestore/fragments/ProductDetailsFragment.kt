package com.project.morestore.fragments

import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.text.toSpannable
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.project.morestore.R
import com.project.morestore.adapters.PhotoViewPagerAdapter
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentProductBinding
import com.project.morestore.models.Product
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*

class ProductDetailsFragment: MvpAppCompatFragment(R.layout.fragment_product), MainMvpView {
    private val binding: FragmentProductBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()
    private val args: ProductDetailsFragmentArgs by navArgs()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initList()
        initToolBar()
        loadYouMayLikeProducts()
        bind(args.product)
    }

    private fun bind(product: Product){
        binding.productPriceTextView.text = "${product.price - ((product.price/100) * product.sale) } ₽"
        val crossedStr = "${product.price} ₽".toSpannable().apply { setSpan(
                StrikethroughSpan(), 0, length ,0) }
            binding.productOldPriceTextView.text = crossedStr

        binding.productNameTextView.text = product.name
        binding.likesCountTextView.text = product.statistic.wishlist.total.toString()
        binding.shareCountTextView.text = product.statistic.share.total.toString()
        binding.discountTextView.text = "-${product.sale}%"
        binding.sellerNameTextView.text = product.user.name
        binding.sellerPhoneTextView.text = product.phoneShow
        binding.productDescriptionTextView.text = product.about
        binding.productNumberTextView.text = product.id.toString()
        binding.productCityTextView.text = product.address.fullCity.name
        val calendar = Calendar.getInstance().apply { timeInMillis =  System.currentTimeMillis() - product.date * 1000 }
        binding.productUpLoadDateTextView.text = "${(System.currentTimeMillis()/1000 - product.date)/86400} дня назад"
        Glide.with(this)
            .load(product.user.avatar?.photo)
            .into(binding.avatarImageView)


    }

    private fun initViewPager(){
        val photoAdapter = PhotoViewPagerAdapter(this)
        photoAdapter.updateList(args.product.photo)
        binding.productPhotoViewPager.adapter = photoAdapter
        binding.viewPagerDots.setViewPager2(binding.productPhotoViewPager)

        //TabLayoutMediator(binding.viewPagerDots, binding.productPhotoViewPager) { tab, position ->
            ////Some implementation
        //}.attach()


    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Вечернее платье"
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_cart)
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initList(){
        productAdapter = ProductAdapter(4){}
        with(binding.productList){
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }

    }

    private fun loadYouMayLikeProducts(){
        presenter.getYouMayLikeProducts()
    }

    private fun initViews(){
        val crossedStr = binding.productOldPriceTextView.text.toSpannable().apply { setSpan(
            StrikethroughSpan(), 0, binding.productOldPriceTextView.text.length ,0) }
        binding.productOldPriceTextView.text = crossedStr
    }

    override fun loaded(result: Any) {
        productAdapter.updateList(result as List<Product>)

    }

    override fun loading() {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>) {

    }
}