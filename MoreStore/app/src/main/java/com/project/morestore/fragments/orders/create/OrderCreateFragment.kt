package com.project.morestore.fragments.orders.create

import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.project.morestore.fragments.MyAddressesFragment
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
                "${chosenAddress?.address?.street}, ${chosenAddress?.address?.building.orEmpty()};${chosenTime?.timeInMillis}",
                chosenTime?.timeInMillis ?: 0/1000)
            presenter.onCreateOrder(args.cartId, deliveryId, place, payId)


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
                chosenAddress = address
                binding.chosenAddressTextView.text = "${address?.address?.street}, ${address?.address?.building.orEmpty()}"
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