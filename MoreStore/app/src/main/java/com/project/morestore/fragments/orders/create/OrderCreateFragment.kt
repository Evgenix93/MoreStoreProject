package com.project.morestore.fragments.orders.create

import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOrderCreateBinding
import com.project.morestore.dialogs.MenuBottomDialogDateFragment
import com.project.morestore.models.Product
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class OrderCreateFragment : MvpAppCompatFragment(R.layout.fragment_order_create), OrderCreateView {
    private val binding: FragmentOrderCreateBinding by viewBinding()

    private val presenter by moxyPresenter { OrderCreatePresenter(requireContext()) }
    private val args: OrderCreateFragmentArgs by navArgs()

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

    ///////////////////////////////////////////////////////////////////////////
    //                      Private
    ///////////////////////////////////////////////////////////////////////////

    private fun setClickListeners() {
        binding.dateEditText.setOnClickListener {
            MenuBottomDialogDateFragment(/*requireContext(),*/ false, { day, month, year ->
                binding.dateEditText.setText("$day.$month.$year")

            }, { _, _ ->

            }).show(childFragmentManager, null)
        }

        binding.timeEditText.setOnClickListener {
            MenuBottomDialogDateFragment(/*requireContext(),*/ true, { _, _, _ ->

            }, { hour, minute ->
                binding.timeEditText.setText("$hour:$minute")


            }).show(childFragmentManager, null)
        }

        binding.cancelTextView.setOnClickListener {
            presenter.onCancelOrderCreateClick()
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
}