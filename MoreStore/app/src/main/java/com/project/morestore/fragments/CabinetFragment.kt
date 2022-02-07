package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentCabinetBinding
import com.project.morestore.models.Filter
import com.project.morestore.models.Product
import com.project.morestore.models.User
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*
import kotlin.math.abs

class CabinetFragment: MvpAppCompatFragment(R.layout.fragment_cabinet), UserMvpView {
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private val binding: FragmentCabinetBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()
    private var emptyActiveListImageRes = R.drawable.glasses_women
    private var emptyOnModerationImageRes = R.drawable.shoe_women

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkToken()
        setClickListeners()
        showBottomNav()
        initToolbar()
        initProductsButtons()
        initList()
        getActiveProducts()
        getFilter()
    }

    private fun checkToken(){
        presenter.checkToken()
    }

    private fun setClickListeners(){
        //binding.logoutBtn.setOnClickListener{
          //  presenter.clearToken()
       // }

        binding.toolbarMain.actionIcon.setOnClickListener {
            presenter.clearToken()
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
        presenter.getUserInfo()
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

    private fun initProductsButtons(){
        binding.activeProductsBtn.setOnClickListener {
            getActiveProducts()
            setUpActiveButton(binding.activeProductsBtn, binding.activeCountTextView, binding.activeProductsTextView)
            binding.glassesImageView.setImageResource(emptyActiveListImageRes)

        }
        binding.onModerationBtn.setOnClickListener {
            getActiveProducts()
            setUpActiveButton(binding.onModerationBtn, binding.onModerationCountTextView, binding.onModerationTextView)
            binding.glassesImageView.setImageResource(emptyOnModerationImageRes)
        }
        binding.archivedProductsBtn.setOnClickListener {
            getActiveProducts()
            setUpActiveButton(binding.archivedProductsBtn, binding.archivedCountTextView, binding.archiveTextView)
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


        btn.background.setTint(ResourcesCompat.getColor(resources, R.color.gray3, null))
        countTextView.background.setTint(ResourcesCompat.getColor(resources, R.color.white, null))
        countTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.green, null))
        listNameTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.green, null))







    }

    private fun getActiveProducts(){
        presenter.getUserProducts()

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
        productAdapter = ProductAdapter(0) {}

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
        findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToFirstLaunchFragment3())
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
                findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToProfileFragment(result))
            }
            return
        }
        if(result is Boolean) {
            val tokenIsEmpty = result as Boolean
            if (tokenIsEmpty) {
                findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToCabinetGuestFragment())
            } else {
                getUserInfo()
            }
        }

        if(result is List<*>){
            showLoading(false)
            binding.noProductsTextView.isVisible = result.isEmpty()
            binding.glassesImageView.isVisible = result.isEmpty()
            binding.createNewProductBtn.isVisible = result.isEmpty()
            binding.createNewProductBtn2.isVisible = result.isNotEmpty()
            binding.productList.isVisible = result.isNotEmpty()
            productAdapter.updateList(result as List<Product>)

        }

        if(result is Filter){
            bindFilter(result)
        }
    }

    override fun successNewCode() {

    }
}