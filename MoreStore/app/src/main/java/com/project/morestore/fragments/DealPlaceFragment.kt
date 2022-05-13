package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentDealPlaceBinding
import com.project.morestore.models.OfferedOrderPlace
import com.project.morestore.models.Order
import com.project.morestore.models.OrderPlace
import com.project.morestore.models.User
import com.project.morestore.models.cart.CartItem
import com.project.morestore.mvpviews.SalesMvpView
import com.project.morestore.presenters.SalesPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*

class DealPlaceFragment: MvpAppCompatFragment(R.layout.fragment_deal_place), SalesMvpView {
    private val binding: FragmentDealPlaceBinding by viewBinding()
    private val presenter by moxyPresenter{SalesPresenter(requireContext())}
    private val args: DealPlaceFragmentArgs by navArgs()
    //private val chosenAddress:

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSaveButton()
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Место сделки"
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_support_black)
        binding.toolbar.backIcon.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun initSaveButton(){
      binding.saveButton.setOnClickListener{
         presenter.addDealPlace(args.orderId, "Москва, кутузовская 5;${System.currentTimeMillis()}")
      }
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onSalesLoaded(sales: List<Order>, addresses: List<OfferedOrderPlace>, users: List<User?>) {

    }

    override fun onUserLoaded() {

    }

    override fun onError(message: String) {
        showToast(message)
    }

    override fun onDealPlaceAdded() {
        showToast("Адрес успешно добавлен")
        findNavController().popBackStack()
    }

    override fun onDealPlaceAccepted() {

    }

    override fun onItemsLoaded(
        cartItems: List<CartItem>,
        activeOrders: List<Order>,
        activeSales: List<Order>,
        inactiveOrders: List<Order>,
        inactiveSales: List<Order>
    ) {

    }
}