package com.project.morestore.presentation.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.project.morestore.R
import com.project.morestore.presentation.adapters.SellerProfileAdapter
import com.project.morestore.databinding.FragmentSellerProfileBinding
import com.project.morestore.data.models.Product
import com.project.morestore.data.models.User
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SellerProfileFragment: MvpAppCompatFragment(R.layout.fragment_seller_profile), UserMvpView {
    private val binding: FragmentSellerProfileBinding by viewBinding()
    @Inject
    lateinit var userPresenter: UserPresenter
    private val presenter by moxyPresenter { userPresenter }
    private val args: SellerProfileFragmentArgs by navArgs()
    private lateinit var sellerProfileAdapter: SellerProfileAdapter
    private var isSellerFavorite = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        getUser()
        setClickListeners()
    }

    private fun initViewPager(list: List<Product>){
        sellerProfileAdapter = SellerProfileAdapter(this, presenter.getUser().id)
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

    private fun getUser(){
        if(args.user != null) {
            showUserInfo(args.user!!)
        }
        else {
            presenter.getSellerInfo(requireArguments().getLong(USER_ID))
        }
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.isVisible = false
        binding.toolbar.filterBtn.isVisible = true
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()

        }
    }

    private fun showUserInfo(user: User){
        presenter.setUser(user)
        presenter.getSellerProducts(user.id)
        presenter.getSellersWishList()
        binding.userNameTextView.text = "${user.name} ${user.surname}"
        val listener =
            MaskedTextChangedListener("+7([000]) [000]-[00]-[00]", binding.userPhoneTextView)
        binding.userPhoneTextView.addTextChangedListener(listener)

        binding.userPhoneTextView.setText(user.phone)
        val calendar = Calendar.getInstance().apply { timeInMillis = user.createdAt!!.toLong() * 1000 }
        Log.d("mylog", "calendar ${calendar.get(Calendar.MONTH)}")
        binding.timeFromRegistrationTextView.text =
            "зарегистрирован(а) ${calendar.get(Calendar.DAY_OF_MONTH)}.${calendar.get(Calendar.MONTH) + 1}.${calendar.get(Calendar.YEAR)}"

        Glide.with(this)
            .load(user.avatar?.photo)
            .into(binding.avatarImageView)

        binding.sellerRatingTextView.text = String.format("%.1f", user.rating?.value).replace(",", ".")

    }

    private fun setClickListeners(){
        binding.subscribeBtn.setOnClickListener {
            presenter.addDeleteSellersInWishList(listOf(presenter.getUser().id))
        }

        binding.profileBtn.setOnClickListener{
           if(presenter.getUser().isBlackList == true)
               Toast.makeText(requireContext(), "Собеседник вас заблокировал", Toast.LENGTH_LONG).show()
            else
                findNavController().navigate(SellerProfileFragmentDirections.actionSellerProfileFragmentToMessagesFragment(presenter.getUser().id))
        }
        binding.shareImageView.setOnClickListener {
            val userId = presenter.getUser().id
            val intent = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "https://more.store/profile/$userId")
            startActivity(intent)
        }
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
        binding.loader.isVisible = false
        isSellerFavorite = isSellerFavorite.not()
        val message = if(isSellerFavorite) "Продавец добален в избранное"
        else "Продавец убран из избранного"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        initSubscribeBtn(isSellerFavorite)

    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
       binding.loader.isVisible = true
    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        when(result){
            is User -> {
                showUserInfo(result)
            }
            is List<*> -> {
                if(result[0] is Product) {
                    val products = result as List<Product>
                    initViewPager(products)
                }
                if(result[0] is User){
                    isSellerFavorite = (result as List<User>).find { it.id == presenter.getUser().id } != null
                    initSubscribeBtn(isSellerFavorite)
                }
            }
        }
    }

    companion object{
        const val USER_ID = "user id"
    }
}