package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
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
import com.project.morestore.models.Chat
import com.project.morestore.models.Product
import com.project.morestore.models.User
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.Token
import com.project.morestore.util.autoCleared
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class SellerProfileFragment: MvpAppCompatFragment(R.layout.fragment_seller_profile), UserMvpView {
    private val binding: FragmentSellerProfileBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private val args: SellerProfileFragmentArgs by navArgs()
    private lateinit var sellerProfileAdapter: SellerProfileAdapter
    private var isSellerFavorite = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        showUserInfo(args.user)
        getSellerProducts()
        setClickListeners()
        getSellersWishList()
    }

    private fun initViewPager(list: List<Product>){
        sellerProfileAdapter = SellerProfileAdapter(this, args.user.id)
        sellerProfileAdapter.updateList(list)
     binding.sellerViewPager.adapter = sellerProfileAdapter
        TabLayoutMediator(binding.tabLayout, binding.sellerViewPager){tab,position ->
            when (position){
                0 -> tab.text = "Объявления"
                1 -> tab.text = "Отзывы"
            }
        }.attach()
        if(args.toReviews)
            binding.sellerViewPager.setCurrentItem(2, false)
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

        binding.sellerRatingTextView.text = user.rating?.value.toString()

    }

    private fun setClickListeners(){
        binding.subscribeBtn.setOnClickListener {
            presenter.addDeleteSellersInWishList(listOf(args.user.id))
        }

        binding.profileBtn.setOnClickListener{
           if(args.user.isBlackList == true)
               Toast.makeText(requireContext(), "Собеседник вас заблокировал", Toast.LENGTH_LONG).show()
        }
    }

    private fun getSellersWishList(){
        presenter.getSellersWishList()
    }

    private fun initSubscribeBtn(isFavorite: Boolean){
        if(isFavorite){
            binding.subscribeBtn.text = "Отписаться"
            binding.subscribeBtn.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black4, null))
            binding.subscribeBtn.setIconResource(R.drawable.ic_x)
        }else{
            binding.subscribeBtn.text = "Подписаться"
            binding.subscribeBtn.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.green, null))
            binding.subscribeBtn.setIconResource(R.drawable.ic_plus)
        }


    }


    override fun success(result: Any) {
        isSellerFavorite = isSellerFavorite.not()
        val message = if(isSellerFavorite) "Продавец добален в избранное"
        else "Продавец убран из избранного"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        initSubscribeBtn(isSellerFavorite)

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        when(result){
            is User -> {
                showUserInfo(result)
            }
            is List<*> -> {
                if(result[0] is Product) {
                    val products = result as List<Product>
                    Log.d("Debug", "loaded products = ${products}")
                    initViewPager(products)
                }
                if(result[0] is User){
                    isSellerFavorite = (result as List<User>).find { it.id == args.user.id } != null
                    initSubscribeBtn(isSellerFavorite)

                }
            }
        }

    }

    override fun successNewCode() {
        TODO("Not yet implemented")
    }
}