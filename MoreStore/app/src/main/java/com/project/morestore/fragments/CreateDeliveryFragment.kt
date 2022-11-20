package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Log
import android.util.Range
import android.view.View
import android.widget.Toast
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentDeliveryCreateBinding
import com.project.morestore.fragments.orders.create.OrderCreateFragment
import com.project.morestore.models.Order
import com.project.morestore.models.Product
import com.project.morestore.models.User
import com.project.morestore.mvpviews.CreateDeliveryMvpView
import com.project.morestore.presenters.CreateDeliveryPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateDeliveryFragment: MvpAppCompatFragment(R.layout.fragment_delivery_create), CreateDeliveryMvpView {
    private val binding: FragmentDeliveryCreateBinding by viewBinding()
    private val args: CreateDeliveryFragmentArgs by navArgs()
    @Inject lateinit var createDeliveryPresenter: CreateDeliveryPresenter
    private val presenter by moxyPresenter { createDeliveryPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProductInfo(args.order.cart?.first()!!)
        presenter.getUserInfo()
        presenter.getProductInfoById(args.order.cart?.first()!!.id)
        setClickListeners()
        setDeliveryInfo(args.order)
    }

    private fun setDeliveryInfo(order: Order){
        if(order.delivery == 2){
            binding.deliveryInfoTextView.text = "Yandex Go"
            binding.dealPlaceTextView.text = "Откуда забрать заказ"
            binding.addressTextView.text = order.cart?.first()?.address?.fullAddress
            Log.d("address", order.cart?.first()?.address?.fullAddress!!)
            binding.icon.setImageResource(R.drawable.ic_package)
            binding.icon.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white, null))
            binding.icon.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blue4, null))
            binding.title.setText(R.string.myAddress_pickup)
        }else {
            binding.dealPlaceTextView.text = "Куда принести заказ"
            binding.addressTextView.text = order.cart?.first()?.addressCdek
            binding.icon.setImageResource(R.drawable.ic_envelope)
            binding.icon.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green, null))
            binding.title.setText(R.string.myAddress_delivery)
        }
    }



    override fun setProductInfo(product: Product){
        Glide.with(this)
            .load(product.photo.first().photo)
            .into(binding.productImageView)

        binding.productNameTextView.text = product.name
        binding.productConditionTextView.text = product.property?.find { it.id == 11L  }?.value
        binding.sizeTextView.text = product.property?.find {
            Range.create(1, 9).contains(it.id.toInt())
        }?.value.orEmpty()
        binding.newPriceTextView.text = product.priceNew.toString()
        val crossedStr = "${product.price} ₽".toSpannable().apply {
            setSpan(
                StrikethroughSpan(), 0, length, 0
            )
        }
        binding.oldPriceTextView.text = crossedStr

    }

    private fun setClickListeners(){
        binding.createOrderBtn.setOnClickListener {
            if(args.order.delivery == OrderCreateFragment.ANOTHER_CITY)
               presenter.createCdekOrder(args.order)
            if(args.order.delivery == OrderCreateFragment.YANDEX_GO)
                presenter.createAndSubmitYandexOrder(args.order)
        }
    }

    override fun setProfileInfo(user: User){
        Glide.with(this)
            .load(user.avatar?.photo)
            .into(binding.sellerAvatarImageView)

    }

    override fun loading(loading: Boolean) {
        binding.loader.isVisible = loading

    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun success() {
        showMessage("заказ создан")
        findNavController().popBackStack()
    }
}