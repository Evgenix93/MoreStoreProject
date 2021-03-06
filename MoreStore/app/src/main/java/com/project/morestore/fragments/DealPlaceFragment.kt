package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentDealPlaceBinding
import com.project.morestore.dialogs.MenuBottomDialogDateFragment
import com.project.morestore.fragments.orders.create.OrderCreateFragment
import com.project.morestore.fragments.orders.create.OrderCreateFragmentDirections
import com.project.morestore.models.*

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
    private var chosenAddressStr = ""

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
          setFragmentResult(ADDRESS_REQUEST, bundleOf(ADDRESS to "$chosenAddressStr;${chosenTime?.timeInMillis}"))
          presenter.addDealPlace(args.orderId, "$chosenAddressStr;${chosenTime?.timeInMillis}")
      }
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun setClickListeners(){
        binding.dateEditText.setOnClickListener {
            MenuBottomDialogDateFragment(/*requireContext(),*/ false, { day, month, year ->
                val currentCalendar = Calendar.getInstance()
                val choiceCalendar = Calendar.getInstance().apply { set(year, month - 1, day) }
                val timeDiff = choiceCalendar.timeInMillis - currentCalendar.timeInMillis
                val currDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
                val currMonth = currentCalendar.get(Calendar.MONTH) + 1
                val currYear = currentCalendar.get(Calendar.YEAR)
                val dayStr = if(day < 10) "0$day" else day.toString()
                val monthStr = if(month < 10) "0$month" else month.toString()
                val yearStr = year.toString().takeLast(2)
                if(timeDiff > 0) {
                    binding.dateEditText.setText("$dayStr.$monthStr.$yearStr")
                    chosenTime?.set(year, month - 1, day)
                    if(chosenTime == null)
                        chosenTime = Calendar.getInstance().apply { set(year, month - 1, day) }

                }
                else{
                    val currDayStr = if(currDay < 10) "0$currDay" else currDay.toString()
                    val currMonthStr = if(currMonth < 10) "0$currMonth" else currMonth.toString()
                    val currYearStr = currYear.toString().takeLast(2)
                    binding.dateEditText.setText("$currDayStr.$currMonthStr.$currYearStr")
                    chosenTime?.set(currYear, currMonth - 1, currDay)

                    if((chosenTime != null) && chosenTime!!.timeInMillis - System.currentTimeMillis() < 3600 * 1000){
                        binding.timeEditText.setText(
                            "${currentCalendar.get(Calendar.HOUR_OF_DAY) + 1}:${
                                currentCalendar.get(
                                    Calendar.MINUTE
                                )
                            }"
                        )
                        chosenTime?.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY) + 1)
                        chosenTime?.set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
                    }
                    if(chosenTime == null)
                        chosenTime = Calendar.getInstance().apply { set(currYear, currMonth - 1, currDay) }
                }


            }, { _, _ ->

            }).show(childFragmentManager, null)

        }

        binding.timeEditText.setOnClickListener {
            MenuBottomDialogDateFragment(/*requireContext(),*/ true, { _, _, _ ->

            }, { hour, minute ->
                val currentCalendar = Calendar.getInstance()
                chosenTime?.set(Calendar.HOUR_OF_DAY, hour)
                chosenTime?.set(Calendar.MINUTE, minute)
                if(chosenTime != null) {
                    val timeDiff = chosenTime!!.timeInMillis - currentCalendar.timeInMillis
                    if(timeDiff > 0)
                        binding.timeEditText.setText("$hour:$minute")
                    else {
                        binding.timeEditText.setText(
                            "${currentCalendar.get(Calendar.HOUR_OF_DAY) + 1}:${
                                currentCalendar.get(
                                    Calendar.MINUTE
                                )
                            }"
                        )
                        chosenTime?.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY) + 1)
                        chosenTime?.set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
                    }

                }else {
                    binding.timeEditText.setText("$hour:$minute")
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
                Log.d("mylog", "address $address")
                chosenAddress = address
                val streetStr = address?.address?.street
                val houseStr = if(address?.address?.house != null) "дом.${address.address.house}" else null
                val housingStr = if(address?.address?.housing != null) "кп.${address.address.housing}" else null
                val buildingStr = if(address?.address?.building != null) "стр.${address.address.building}" else null
                val apartmentStr = if(address?.address?.apartment != null) "кв.${address.address.apartment}" else null
                val strings =
                    arrayOf(streetStr, houseStr, housingStr, buildingStr, apartmentStr).filterNotNull()
                binding.chosenAddressTextView.text = strings.joinToString(", ")
                chosenAddressStr = strings.joinToString(", ")
                binding.chosenAddressTextView.isVisible = true
                binding.chooseOnMapTextView.text = "Изменить"



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
        users: List<User?>,
        dialogs: List<DialogWrapper>
    ) {
        TODO("Not yet implemented")
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

    override fun onDealStatusChanged() {
        TODO("Not yet implemented")
    }

    companion object{
        const val ADDRESS_REQUEST = "address key"
        const val ADDRESS = "address"
    }
}