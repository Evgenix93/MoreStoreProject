package com.project.morestore.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentCabinetBinding
import com.project.morestore.fragments.base.BottomNavigationMvpFragment
import com.project.morestore.models.Filter
import com.project.morestore.models.Product
import com.project.morestore.models.User
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.ktx.moxyPresenter
import java.util.*

class CabinetFragment: BottomNavigationMvpFragment(R.layout.fragment_cabinet), UserMvpView {
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private val binding: FragmentCabinetBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()
    private var emptyActiveListImageRes = R.drawable.glasses_women
    private var emptyOnModerationImageRes = R.drawable.shoe_women

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        showBottomNav()
        initToolbar()
        initProductsButtons()
        initList()
        getFilter()
        checkToken()
    }

    private fun checkToken(){
        presenter.checkToken()
    }

    private fun setClickListeners(){

        binding.toolbarMain.actionIcon.setOnClickListener {
            presenter.clearToken()
        }

       binding.createNewProductBtn.setOnClickListener{
           findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToCreateProductStep1Fragment())
       }
        binding.createNewProductBtn2.setOnClickListener{
            findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToCreateProductStep1Fragment())
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

        binding.userPhoneTextView.setText(user.phone)
        val diffSeconds = System.currentTimeMillis()/1000 - user.createdAt!!.toLong()
        val dayDiff = diffSeconds/86400
        val monthDiff = dayDiff/30
        //binding.timeFromRegistrationTextView.text = if(monthDiff > 0)"зарегистрирован(а) $monthDiff месяца назад"
        //else "зарегистрирован(а) $dayDiff дня назад"
        val calendar = Calendar.getInstance().apply { timeInMillis = user.createdAt.toLong() * 1000 }
        binding.timeFromRegistrationTextView.text =
            "зарегистрирован(а) ${calendar.get(Calendar.DAY_OF_MONTH)}.${calendar.get(Calendar.MONTH) + 1}.${calendar.get(
                Calendar.YEAR)}"

        Glide.with(this)
            .load(user.avatar?.photo.toString())
            .into(binding.avatarImageView)

        binding.userRatingTextView.text = user.rating?.value.toString()

    }

    private fun initProductsButtons(){
        binding.activeProductsBtn.setOnClickListener {
            getProducts(true)
            setUpActiveButton(binding.activeProductsBtn, binding.activeCountTextView, binding.activeProductsTextView)
            binding.glassesImageView.setImageResource(emptyActiveListImageRes)

        }
        binding.onModerationBtn.setOnClickListener {
            productAdapter.updateList(emptyList())
            setUpActiveButton(binding.onModerationBtn, binding.onModerationCountTextView, binding.onModerationTextView)
            binding.glassesImageView.setImageResource(emptyOnModerationImageRes)
        }
        binding.archivedProductsBtn.setOnClickListener {
            getProducts(false)
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

    private fun getProducts(isActive: Boolean){
        Log.d("MyDebug", "getActiveProducts")
        presenter.getUserProducts(isActive)
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
        productAdapter = ProductAdapter(null) {
            Log.d("MyDebug", "onProduct Click")
            findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToProductDetailsFragment(it, null, true))
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
            getProducts(true)
            return
        }
        if(result is Boolean) {
            val tokenIsEmpty = result as Boolean
            if (tokenIsEmpty) {
                val navOptions =  NavOptions.Builder().setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false).build()
                findNavController().navigate(R.id.cabinetGuestFragment, null , navOptions)
            } else {
                getUserInfo()
            }
        }

        if(result is List<*>){
            Log.d("MyDebug", "list is loaded ${result as List<Product>}")
            showLoading(false)
            binding.noProductsTextView.isVisible = result.isEmpty()
            binding.glassesImageView.isVisible = result.isEmpty()
            binding.createNewProductBtn.isVisible = result.isEmpty()
            binding.createNewProductBtn2.isVisible = result.isNotEmpty()
            binding.emptyScrollView.isVisible = false
            binding.productList.isVisible = result.isNotEmpty()
            productAdapter.updateList(result as List<Product>)
            when{
                result.firstOrNull()?.status == 1 || result.firstOrNull()?.status == 6 ->
                    binding.activeCountTextView.text = result.size.toString()
                result.firstOrNull()?.status == 8 -> binding.archivedCountTextView.text = result.size.toString()
            }

        }

        if(result is Filter){
            bindFilter(result)
        }
    }

    override fun successNewCode() {

    }
}