package com.project.morestore.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.presentation.adapters.ProductAdapter
import com.project.morestore.presentation.adapters.ReviewsAdapter

import com.project.morestore.presentation.fragments.base.BottomNavigationFragment

import com.project.morestore.data.models.Filter
import com.project.morestore.data.models.Product
import com.project.morestore.data.models.ReviewItem
import com.project.morestore.data.models.User
import com.project.morestore.databinding.FragmentMyProductsBinding
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyProductsFragment: BottomNavigationFragment(R.layout.fragment_my_products), UserMvpView {
    @Inject lateinit var userPresenter: UserPresenter
    private val presenter by moxyPresenter { userPresenter }
    private val binding: FragmentMyProductsBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()
    private var reviewsAdapter: ReviewsAdapter by autoCleared()
    private var emptyActiveListImageRes = R.drawable.glasses_women
    private var emptyOnModerationImageRes = R.drawable.shoe_women
    private val args: MyProductsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkToken()
        setClickListeners()
        showBottomNav()
        initToolbar()
        initProductsButtons()
        initList()
        getFilter()
        showSelectedProducts()
    }

    private fun showSelectedProducts(){
        when(args.productTypeInt){
            0 -> binding.activeProductsBtn.callOnClick()
            1 -> binding.onModerationBtn.callOnClick()
            2 -> binding.archivedProductsBtn.callOnClick()
            3 -> binding.reviewsBtn.callOnClick()
        }
    }

    private fun checkToken(){
        presenter.checkToken()
    }

    private fun setClickListeners(){

        binding.toolbarMain.actionIcon.setOnClickListener {
            presenter.clearToken()
        }

       binding.createNewProductBtn.setOnClickListener{
           findNavController().navigate(MyProductsFragmentDirections.actionCabinetFragmentToCreateProductStep1Fragment())
       }
        binding.createNewProductBtn2.setOnClickListener{
            findNavController().navigate(MyProductsFragmentDirections.actionCabinetFragmentToCreateProductStep1Fragment())
        }
        binding.myAddresses.setOnClickListener { findNavController().navigate(R.id.myAddressesFragment) }

        binding.shareImageView.setOnClickListener{
           val userId = presenter.getUser().id
           val intent = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "https://morestore.ru/users/$userId")
           startActivity(intent)
        }
    }

    private fun showBottomNav(){
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun initToolbar(){
        binding.toolbarMain.actionIcon.setImageResource(R.drawable.ic_signout)
        binding.toolbarMain.titleTextView.text = "Кабинет"
        binding.toolbarMain.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getUserInfo(){
        presenter.getUserData()
    }

    private fun showUserInfo(user: User){
        binding.userNameTextView.text = "${user.name} ${user.surname}"
        val listener =
            MaskedTextChangedListener("+7([000]) [000]-[00]-[00]", binding.userPhoneTextView)
        binding.userPhoneTextView.addTextChangedListener(listener)

        binding.userPhoneTextView.setText(user.phone?.replaceFirstChar {'7'})
        val diffSeconds = System.currentTimeMillis()/1000 - user.createdAt!!.toLong()
        val dayDiff = diffSeconds/86400
        val monthDiff = dayDiff/30
        Log.d("mylog", "current time ${System.currentTimeMillis()}")
        val calendar = Calendar.getInstance().apply { timeInMillis = user.createdAt.toLong() * 1000 }
        binding.timeFromRegistrationTextView.text =
            "зарегистрирован(а) ${calendar.get(Calendar.DAY_OF_MONTH)}.${calendar.get(Calendar.MONTH) + 1}.${calendar.get(
                Calendar.YEAR)}"

        Glide.with(this)
            .load(user.avatar?.photo.toString())
            .into(binding.avatarImageView)

        binding.userRatingTextView.text = String.format("%.1f", user.rating?.value).replace(",", ".")

    }

    private fun initProductsButtons(){
        binding.activeProductsBtn.setOnClickListener {
            getProducts(true, false)
            setUpActiveButton(binding.activeProductsBtn, binding.activeCountTextView, binding.activeProductsTextView)
            binding.glassesImageView.setImageResource(emptyActiveListImageRes)

        }
        binding.onModerationBtn.setOnClickListener {
            getProducts(true, true)
            setUpActiveButton(binding.onModerationBtn, binding.onModerationCountTextView, binding.onModerationTextView)
            binding.glassesImageView.setImageResource(emptyOnModerationImageRes)
        }
        binding.archivedProductsBtn.setOnClickListener {
            getProducts(false, false)
            setUpActiveButton(binding.archivedProductsBtn, binding.archivedCountTextView, binding.archiveTextView)
        }
        binding.reviewsBtn.setOnClickListener {
            presenter.getCurrentUserReviews()
            setUpActiveButton(binding.reviewsBtn, binding.reviewsCountTextView, binding.reviewsTextView)
        }


    }

    private fun setUpActiveButton(btn: FrameLayout, countTextView: TextView, listNameTextView: TextView){
        binding.activeProductsBtn.background.setTint(ResourcesCompat.getColor(resources, R.color.white, null))
        binding.activeCountTextView.background.setTint(ResourcesCompat.getColor(resources, R.color.gray3, null))
        binding.activeProductsTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
        binding.activeCountTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.gray2, null))


        binding.onModerationBtn.background.setTint(ResourcesCompat.getColor(resources, R.color.white, null))
        binding.onModerationCountTextView.background.setTint(ResourcesCompat.getColor(resources, R.color.gray3, null))
        binding.onModerationTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
        binding.onModerationCountTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.gray2, null))


        binding.archivedProductsBtn.background.setTint(ResourcesCompat.getColor(resources, R.color.white, null))
        binding.archivedCountTextView.background.setTint(ResourcesCompat.getColor(resources, R.color.gray3, null))
        binding.archiveTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
        binding.archivedCountTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.gray2, null))


        binding.reviewsBtn.background.setTint(ResourcesCompat.getColor(resources, R.color.white, null))
        binding.reviewsCountTextView.background.setTint(ResourcesCompat.getColor(resources, R.color.gray3, null))
        binding.reviewsTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
        binding.reviewsCountTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.gray2, null))


        btn.background.setTint(ResourcesCompat.getColor(resources, R.color.gray3, null))
        countTextView.background.setTint(ResourcesCompat.getColor(resources, R.color.white, null))
        countTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.green, null))
        listNameTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.green, null))
    }

    private fun getProducts(isActive: Boolean, isOnModeration: Boolean){
        Log.d("MyDebug", "getActiveProducts")
        presenter.getUserProducts(isActive, isOnModeration)
    }

    private fun getFilter(){
        presenter.getFilter()
    }

    private fun bindFilter(filter: Filter){
        when{
            filter.chosenForWho[0] -> {
                emptyActiveListImageRes = R.drawable.glasses_women
                emptyOnModerationImageRes = R.drawable.shoe_women
                binding.glassesImageView.setImageResource(R.drawable.glasses_women)
            }
            filter.chosenForWho[1] -> {
                emptyActiveListImageRes = R.drawable.glasses_men
                emptyOnModerationImageRes = R.drawable.shoe_men
                binding.glassesImageView.setImageResource(R.drawable.glasses_men)

            }
        }

    }

    private fun initList(){
        reviewsAdapter = ReviewsAdapter({}, {
            val photosArray = it.photo?.map { it.photo }?.toTypedArray()!!
            findNavController().navigate(
                R.id.mediaFragment, bundleOf(MediaFragment.PHOTOS to photosArray )
            )
        })
        productAdapter = ProductAdapter(null) {
            Log.d("MyDebug", "onProduct Click")
            findNavController().navigate(MyProductsFragmentDirections.actionCabinetFragmentToProductDetailsFragment(it, null, true))
        }

        with(binding.productList){
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }

    override fun success(result: Any) {
        showLoading(false)
        findNavController().navigate(MyProductsFragmentDirections.actionCabinetFragmentToFirstLaunchFragment3())
    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
        showLoading(true)
    }

    override fun loaded(result: Any) {

        if(result is User){
            showLoading(false)
            showUserInfo(result)
            binding.profileBtn.setOnClickListener {
                findNavController().navigate(MyProductsFragmentDirections.actionCabinetFragmentToProfileFragment(result))
            }
            if(args.productTypeInt == -1)
               getProducts(true, false)
            return
        }
        if(result is Boolean) {
            if (result) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false)
                    .build()
                findNavController().navigate(R.id.cabinetGuestFragment, null, navOptions)
            } else {
                getUserInfo()
            }
        }

        if(result is List<*> && result.isNotEmpty()) {
            if (result.firstOrNull() is Product) {
                binding.productList.setSpace(0.dp)
                binding.productList.adapter = productAdapter
                binding.productList.layoutManager = GridLayoutManager(requireContext(), 2)
                Log.d("MyDebug", "list is loaded ${result as List<Product>}")
                showLoading(false)
                binding.noProductsTextView.isVisible = result.isEmpty()
                binding.glassesImageView.isVisible = result.isEmpty()
                binding.createNewProductBtn.isVisible = result.isEmpty()
                binding.createNewProductBtn2.isVisible = result.isNotEmpty()
                binding.emptyScrollView.isVisible = false
                binding.productList.isVisible = result.isNotEmpty()
                productAdapter.updateList(result)
            } else if(result.firstOrNull() is Int) {
                val sizes = result as List<Int>
                binding.activeCountTextView.text = sizes[0].toString()
                binding.onModerationCountTextView.text = sizes[1].toString()
                binding.archivedCountTextView.text = sizes[2].toString()
                binding.reviewsCountTextView.text = sizes[3].toString()
            }else {
                Log.d("mylog", "reviews loaded")
                Log.d("mylog", "reviews $result")
                showLoading(false)
                binding.productList.isVisible = true
                binding.createNewProductBtn.isVisible = false
                binding.createNewProductBtn2.isVisible = true
                binding.productList.setSpace(8.dp)
                binding.productList.adapter = reviewsAdapter
                binding.productList.layoutManager = LinearLayoutManager(requireContext())
                reviewsAdapter.setItems(result as List<ReviewItem>)
            }
        }

        if(result is List<*> && result.isEmpty()){
            showLoading(false)
            productAdapter.updateList(emptyList())
            reviewsAdapter.setItems(emptyList())
            binding.glassesImageView.isVisible = true

        }

        if(result is Filter){
            bindFilter(result)
        }

    }
}