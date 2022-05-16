package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentDealPlaceBinding
import com.project.morestore.dialogs.MenuBottomDialogDateFragment
import com.project.morestore.fragments.orders.create.OrderCreateFragment
import com.project.morestore.fragments.orders.create.OrderCreateFragmentDirections
import com.project.morestore.models.MyAddress
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
    private var chosenAddress: MyAddress? = null
    private var chosenTime: Calendar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSaveButton()
        setClickListeners()
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
          if(chosenAddress == null || chosenTime == null){
              Toast.makeText(requireContext(), "Выберите адрес и время", Toast.LENGTH_SHORT).show()
              return@setOnClickListener
          }
          presenter.addDealPlace(args.orderId, "${chosenAddress?.address?.street},${chosenAddress?.address?.building.orEmpty()};${chosenTime?.timeInMillis}")
      }
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun setClickListeners(){
        binding.dateEditText.setOnClickListener {
            MenuBottomDialogDateFragment(/*requireContext(),*/ false, { day, month, year ->
                binding.dateEditText.setText("$day.$month.$year")
                chosenTime?.set(year, month - 1, day)
                if(chosenTime == null)
                    chosenTime = Calendar.getInstance().apply { set(year, month - 1, day) }

            }, { _, _ ->

            }).show(childFragmentManager, null)
        }

        binding.timeEditText.setOnClickListener {
            MenuBottomDialogDateFragment(/*requireContext(),*/ true, { _, _, _ ->

            }, { hour, minute ->
                binding.timeEditText.setText("$hour:$minute")
                chosenTime?.set(Calendar.HOUR_OF_DAY, hour)
                chosenTime?.set(Calendar.MINUTE, minute)
                if(chosenTime == null){
                    chosenTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }
                }


            }).show(childFragmentManager, null)
        }

        //binding.cancelTextView.setOnClickListener {
          //  presenter.onCancelOrderCreateClick()
        //}

        /*binding.payButton.setOnClickListener {
            val deliveryId = when(binding.deliveryVariantRadioGroup.checkedRadioButtonId){
                R.id.yandexRadioBtn -> OrderCreateFragment.YANDEX_GO
                R.id.anotherCityRadioBtn -> OrderCreateFragment.ANOTHER_CITY
                R.id.takeFromSellerRadioBtn -> OrderCreateFragment.TAKE_FROM_SELLER
                else -> -1
            }
            val placeId = when(binding.radioButtons.checkedRadioButtonId){
                R.id.onSellerChoiceRadioBtn -> OrderCreateFragment.PLACE_FROM_SELLER
                R.id.userVariantRadioBtn -> OrderCreateFragment.PLACE_FROM_ME
                else -> -1
            }
            val payId = when(binding.deliveryTypeRadioGroup.checkedRadioButtonId){
                R.id.onDealPlaceRadioButton -> OrderCreateFragment.PAY_ON_PLACE
                R.id.prepaymentRadioButton -> OrderCreateFragment.PAY_PREPAYMENT
                else -> -1
            }
            val place = if(placeId == OrderCreateFragment.PLACE_FROM_SELLER) OrderPlace(placeId.toLong(), null, null)
            else OrderPlace(placeId.toLong(),
                "${chosenAddress?.address?.street}, ${chosenAddress?.address?.building}",
                chosenTime?.timeInMillis ?: 0/1000)
            presenter.onCreateOrder(args.cartId, deliveryId, place, payId)


        }*/

        binding.chooseOnMapTextView.setOnClickListener {
            /*val deliveryId = when (binding.deliveryVariantRadioGroup.checkedRadioButtonId) {
                R.id.yandexRadioBtn -> OrderCreateFragment.YANDEX_GO
                R.id.anotherCityRadioBtn -> OrderCreateFragment.ANOTHER_CITY
                R.id.takeFromSellerRadioBtn -> OrderCreateFragment.TAKE_FROM_SELLER
                else -> -1
            }*/
            setFragmentResultListener(MyAddressesFragment.ADDRESS_REQUEST){_, bundle ->
                val address = bundle.getParcelable<MyAddress>(MyAddressesFragment.ADDRESS_KEY)
                chosenAddress = address
                binding.chosenAddressTextView.text = "${address?.address?.street}, ${address?.address?.building.orEmpty()}"
                binding.chosenAddressTextView.isVisible = true


            }
            findNavController().navigate(
                DealPlaceFragmentDirections
                .actionDealPlaceFragmentToMyAddressesFragment(true,
                    MyAddressesFragment.ADDRESSES_HOME))
        }

    }



    override fun onSalesLoaded(
        sales: List<Order>,
        addresses: List<OfferedOrderPlace>,
        users: List<User?>
    ) {

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