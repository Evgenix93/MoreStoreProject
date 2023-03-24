package com.project.morestore.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.data.models.User
import com.project.morestore.databinding.FragmentCabinetBinding
import com.project.morestore.domain.presenters.CabinetPresenter
import com.project.morestore.presentation.fragments.base.BottomNavigationFragment
import com.project.morestore.presentation.mvpviews.CabinetMvpView
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CabinetFragment: BottomNavigationFragment(R.layout.fragment_cabinet), CabinetMvpView {
    private val binding: FragmentCabinetBinding by viewBinding()
    @Inject
    lateinit var cabinetPresenter: CabinetPresenter
    private val presenter: CabinetPresenter by moxyPresenter { cabinetPresenter }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkToken()
        showBottomNav()
        initToolbar()
        setClickListeners()
        presenter.getSalesCount()
        presenter.getOrdersCount()

    }


    private fun checkToken() {
        presenter.checkToken()
    }

    private fun showBottomNav() {
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun initToolbar() {
        binding.toolbarMain.actionIcon.setImageResource(R.drawable.ic_signout)
        binding.toolbarMain.titleTextView.text = "Кабинет"
        binding.toolbarMain.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getUserInfo() {
        presenter.getUserData()
    }

    private fun showUserInfo(user: User) {
        binding.userNameTextView.text = "${user.name} ${user.surname}"
        val listener =
            MaskedTextChangedListener("+7([000]) [000]-[00]-[00]", binding.userPhoneTextView)
        binding.userPhoneTextView.addTextChangedListener(listener)

        binding.userPhoneTextView.setText(user.phone?.replaceFirstChar { '7' })
        val diffSeconds = System.currentTimeMillis() / 1000 - user.createdAt!!.toLong()
        val dayDiff = diffSeconds / 86400
        val monthDiff = dayDiff / 30
        Log.d("mylog", "current time ${System.currentTimeMillis()}")
        val calendar =
            Calendar.getInstance().apply { timeInMillis = user.createdAt.toLong() * 1000 }
        binding.timeFromRegistrationTextView.text =
            "зарегистрирован(а) ${calendar.get(Calendar.DAY_OF_MONTH)}.${calendar.get(Calendar.MONTH) + 1}.${
                calendar.get(
                    Calendar.YEAR
                )
            }"

        Glide.with(this)
            .load(user.avatar?.photo.toString())
            .into(binding.avatarImageView)

        binding.userRatingTextView.text =
            String.format("%.1f", user.rating?.value).replace(",", ".")

    }


    override fun showSalesCount(activeSalesCount: Int, finishedSalesCount: Int) {
        binding.inProcessSalesCountTextView.text = activeSalesCount.toString()
        binding.finishedSalesCountTextView.text = finishedSalesCount.toString()
    }

    override fun showOrdersCount(activeOrdersCount: Int, finishedOrdersCount: Int) {
        binding.inProcessOrdersCountTextView.text = activeOrdersCount.toString()
        binding.finishedOrdersCountTextView.text = finishedOrdersCount.toString()
    }

    override fun showReviewCount(count: Int) {
        binding.reviewsCountTextView.text = count.toString()
    }

    private fun getProductsCounts() {
        presenter.getUserProductsCounts()
    }

    private fun getReviewCount(){
        presenter.getReviewCount()
    }

    private fun setClickListeners() {
        binding.activeProductsTextView.setOnClickListener {
            findNavController().navigate(
                CabinetFragmentDirections.actionCabinetFragmentToMyProductsFragment(
                    0
                )
            )
        }
        binding.onModerationTextView.setOnClickListener {
            findNavController().navigate(
                CabinetFragmentDirections.actionCabinetFragmentToMyProductsFragment(
                    1
                )
            )
        }
        binding.archiveProductsTextView.setOnClickListener {
            findNavController().navigate(
                CabinetFragmentDirections.actionCabinetFragmentToMyProductsFragment(
                    2
                )
            )
        }
        binding.draftProductsTextView.setOnClickListener {
            findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToProductDraftFragment())
        }
        binding.inProcessSalesTextView.setOnClickListener {
            findNavController().navigate(R.id.salesActiveFragment)
        }
        binding.finishedSalesTextView.setOnClickListener {
            findNavController().navigate(R.id.salesHistoryFragment)
        }

        binding.inProcessOrdersTextView.setOnClickListener {
            findNavController().navigate(R.id.ordersActiveFragment)
        }

        binding.finishedOrdersTextView.setOnClickListener {
            findNavController().navigate(R.id.ordersHistoryFragment)
        }
        binding.reviewsTextView.setOnClickListener {
            findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToMyProductsFragment(3))
        }
        binding.createNewProductBtn2.setOnClickListener {
            findNavController().navigate(R.id.createProductStep1Fragment)
        }
        binding.shareImageView.setOnClickListener{
            val userId = presenter.getUser().id
            val intent = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "https://morestore.ru/users/$userId")
            startActivity(intent)
        }
        binding.toolbarMain.actionIcon.setOnClickListener {
            presenter.clearToken()
            findNavController().navigate(R.id.firstLaunchFragment)
        }
    }

    override fun isLoggedIn(loggedIn: Boolean) {
        if (!loggedIn) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false)
                .build()
            findNavController().navigate(R.id.cabinetGuestFragment, null, navOptions)
        } else {
            getUserInfo()

        }
    }

    override fun loading(isLoading: Boolean) {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun currentUserLoaded(user: User) {
        showUserInfo(user)
        binding.profileBtn.setOnClickListener {
            findNavController().navigate(
                CabinetFragmentDirections.actionCabinetFragmentToProfileFragment2(
                    user
                )
            )
        }
        binding.myAddresses.setOnClickListener {
            findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToMyAddressesFragment())
        }
        getProductsCounts()
        getReviewCount()
    }

    override fun showProductsCounts(countList: List<Int>) {
        binding.activeProductsCountTextView.text = countList[0].toString()
        binding.onModerationProductsCountTextView.text = countList[1].toString()
        binding.archiveProductsCountTextView.text = countList[2].toString()
        binding.draftProductsCountTextView.text = countList[3].toString()
    }

}

