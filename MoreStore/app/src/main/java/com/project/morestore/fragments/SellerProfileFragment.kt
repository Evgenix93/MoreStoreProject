package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.adapters.SellerProfileAdapter
import com.project.morestore.databinding.FragmentSellerProfileBinding
import com.project.morestore.models.Product
import com.project.morestore.models.User
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class SellerProfileFragment: MvpAppCompatFragment(R.layout.fragment_seller_profile), UserMvpView {
    private val binding: FragmentSellerProfileBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private val args: SellerProfileFragmentArgs by navArgs()
    private lateinit var sellerProfileAdapter: SellerProfileAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        showUserInfo(args.user)
        getSellerProducts()
    }

    private fun initViewPager(list: List<Product>){
        sellerProfileAdapter = SellerProfileAdapter(this)
        sellerProfileAdapter.updateList(list)
     binding.sellerViewPager.adapter = sellerProfileAdapter
        TabLayoutMediator(binding.tabLayout, binding.sellerViewPager){tab,position ->
            when (position){
                0 -> tab.text = "Объявления"
                1 -> tab.text = "Отзывы"
            }
        }.attach()
    }

    private fun getSellerProducts(){
        presenter.getSellerProducts(args.user.id)
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.isVisible = false
        binding.toolbar.filterBtn.isVisible = true
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showUserInfo(user: User){
        binding.userNameTextView.text = "${user.name} ${user.surname}"
        val listener =
            MaskedTextChangedListener("+7([000]) [000]-[00]-[00]", binding.userPhoneTextView)
        binding.userPhoneTextView.addTextChangedListener(listener)

        binding.userPhoneTextView.setText(user.phone)
        val diffSeconds = System.currentTimeMillis()/1000 - user.createdAt!!.toLong()
        val dayDiff = diffSeconds/86400
        val monthDiff = dayDiff/30
        binding.timeFromRegistrationTextView.text = if(monthDiff > 0)"зарегистрирован(а) $monthDiff месяца назад"
        else "зарегистрирован(а) $dayDiff дня назад"

        Glide.with(this)
            .load(user.avatar?.photo)
            .into(binding.avatarImageView)


    }

    override fun success(result: Any) {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
        TODO("Not yet implemented")
    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        when(result){
            is User -> {
                showUserInfo(result)
            }
            is List<*> -> {
                val products = result as List<Product>
                Log.d("Debug", "loaded products = ${products}")
                //sellerProfileAdapter.updateList(result as List<Product>)
                initViewPager(products)
            }
        }

    }

    override fun successNewCode() {
        TODO("Not yet implemented")
    }
}