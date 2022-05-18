package com.project.morestore.fragments.orders.create

import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Log
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOrderCreateBinding
import com.project.morestore.dialogs.MenuBottomDialogDateFragment
import com.project.morestore.fragments.ChatFragment
import com.project.morestore.fragments.MyAddressesFragment
import com.project.morestore.models.Chat
import com.project.morestore.models.MyAddress
import com.project.morestore.models.OrderPlace
import com.project.morestore.models.Product
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*

class OrderCreateFragment : MvpAppCompatFragment(R.layout.fragment_order_create), OrderCreateView {
    private val binding: FragmentOrderCreateBinding by viewBinding()

    private val presenter by moxyPresenter { OrderCreatePresenter(requireContext()) }
    private val args: OrderCreateFragmentArgs by navArgs()
    private var chosenAddress: MyAddress? = null
    private var chosenTime: Calendar? = null//Calendar.getInstance()
    private var chosenAddressStr = ""

    ///////////////////////////////////////////////////////////////////////////
    //                      View
    //////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.setProduct(args.product);
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        setClickListeners()
        initRadioPlaceButtons()
        initRadioDeliveryButtons()
        initViews()
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    override fun navigate(pageId: Int?) {
        when (pageId) {
            null -> {
                findNavController().popBackStack()
            }
            R.id.ordersActiveFragment -> {
                findNavController().navigate(R.id.ordersActiveFragment)
            }
            R.id.chatFragment -> {
                findNavController().navigate(
                    R.id.chatFragment,
                    bundleOf(
                        ChatFragment.USER_ID_KEY to args.product.user?.id,
                        ChatFragment.PRODUCT_ID_KEY to args.product.id,
                        Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                    )
                )
            }
        }
    }

    override fun setProductInfo(product: Product) {
        Glide.with(this)
                .load(product.user?.avatar?.photo.toString())
                .into(binding.sellerAvatarImageView)
        binding.sellerNameTextView.text = product.user?.name


        Glide.with(this)
                .load(product.photo[0].photo)
                .into(binding.productImageView)

        binding.productNameTextView.text = product.name
        binding.productConditionTextView.text = product.property?.find {
            it.name == "Состояние"
        }?.value

        binding.sizeTextView.text = product.property?.find {
            Range.create(1, 9).contains(it.id.toInt())
        }?.value.orEmpty()

        val crossedStr = "${product.price} ₽".toSpannable().apply {
            setSpan(
                    StrikethroughSpan(), 0, length, 0
            )
        }
        binding.oldPriceTextView.text = crossedStr
        binding.newPriceTextView.text = product.priceNew.toString()
    }

    override fun loading() {

    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      Private
    ///////////////////////////////////////////////////////////////////////////

    private fun setClickListeners() {
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

        binding.cancelTextView.setOnClickListener {
            presenter.onCancelOrderCreateClick()
        }

        binding.payButton.setOnClickListener {
            val deliveryId = when(binding.deliveryVariantRadioGroup.checkedRadioButtonId){
                R.id.yandexRadioBtn -> YANDEX_GO
                R.id.anotherCityRadioBtn -> ANOTHER_CITY
                R.id.takeFromSellerRadioBtn -> TAKE_FROM_SELLER
                else -> -1
            }
            val placeId = when(binding.radioButtons.checkedRadioButtonId){
                R.id.onSellerChoiceRadioBtn -> PLACE_FROM_SELLER
                R.id.userVariantRadioBtn -> PLACE_FROM_ME
                else -> -1
            }
            val payId = when(binding.deliveryTypeRadioGroup.checkedRadioButtonId){
                R.id.onDealPlaceRadioButton -> PAY_ON_PLACE
                R.id.prepaymentRadioButton -> PAY_PREPAYMENT
                else -> -1
            }
            val place = if(placeId == PLACE_FROM_SELLER) OrderPlace(placeId.toLong(), null, null)
            else OrderPlace(placeId.toLong(),
                if(chosenAddressStr.isNotEmpty()) "$chosenAddressStr;${chosenTime?.timeInMillis}" else null,
                chosenTime?.timeInMillis ?: 0/1000)
            presenter.onCreateOrder(args.cartId, deliveryId, place, payId,
                findNavController().previousBackStackEntry?.destination?.id == R.id.chatFragment, args.product)


        }

        binding.chooseOnMapTextView.setOnClickListener {
            val deliveryId = when (binding.deliveryVariantRadioGroup.checkedRadioButtonId) {
                R.id.yandexRadioBtn -> YANDEX_GO
                R.id.anotherCityRadioBtn -> ANOTHER_CITY
                R.id.takeFromSellerRadioBtn -> TAKE_FROM_SELLER
                else -> -1
            }
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
            findNavController().navigate(OrderCreateFragmentDirections
                .actionCreateOrderFragmentToMyAddressesFragment(true,
                if(deliveryId == ANOTHER_CITY) MyAddressesFragment.ADDRESSES_CDEK else MyAddressesFragment.ADDRESSES_HOME))
        }

    }

    private fun initRadioPlaceButtons() {
        binding.radioButtons.setOnCheckedChangeListener { radioGroup, id ->
            binding.chooseOnMapTextView.isVisible = id == R.id.userVariantRadioBtn
            binding.placeIcon.isVisible = id == R.id.userVariantRadioBtn
            binding.whenReceiveTextView.isVisible = id == R.id.userVariantRadioBtn
            binding.pickers.isVisible = id == R.id.userVariantRadioBtn
            binding.chosenAddressTextView.isVisible = id == R.id.userVariantRadioBtn && chosenAddress != null
        }
    }

    private fun initViews() {
        val oldPriceStr = binding.oldPriceTextView.text.toSpannable().apply { setSpan(StrikethroughSpan(), 0, length, 0) }
        binding.oldPriceTextView.text = oldPriceStr
        binding.oldPrice2TextView.text = oldPriceStr
    }


    private fun initRadioDeliveryButtons() {
        binding.deliveryTypeRadioGroup.setOnCheckedChangeListener { _, radioButton ->
            binding.prepaymentInfoTextView.isVisible = radioButton == R.id.prepaymentRadioButton
            binding.totalCardView.isVisible = radioButton == R.id.prepaymentRadioButton
            binding.payNowButton.isVisible = radioButton == R.id.prepaymentRadioButton
            binding.payButton.isVisible = radioButton != R.id.prepaymentRadioButton
        }
    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Оформление заказа"
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_support_black)
        binding.toolbar.backIcon.setOnClickListener {
            presenter.onBackClick()
        }
    }

    companion object{
        const val TAKE_FROM_SELLER = 1
        const val YANDEX_GO = 2
        const val ANOTHER_CITY = 3

        const val PLACE_FROM_SELLER = 1
        const val PLACE_FROM_ME = 2

        const val PAY_ON_PLACE = 1
        const val PAY_PREPAYMENT = 2
    }
}