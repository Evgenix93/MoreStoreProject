package com.project.morestore.fragments

import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Range
import android.view.View
import android.widget.Toast
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentDeliveryCreateBinding
import com.project.morestore.models.Product
import com.project.morestore.models.User
import com.project.morestore.mvpviews.CreateDeliveryMvpView
import com.project.morestore.presenters.CreateDeliveryPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateDeliveryFragment: MvpAppCompatFragment(R.layout.fragment_delivery_create), CreateDeliveryMvpView {
    private val binding: FragmentDeliveryCreateBinding by viewBinding()
    private val args: CreateDeliveryFragmentArgs by navArgs()
    private val presenter by moxyPresenter { CreateDeliveryPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProductInfo(args.order.product)
        presenter.getUserInfo()
        setClickListeners()
    }



    private fun setProductInfo(product: Product){
        Glide.with(this)
            .load(product.photo.first().photo)
            .into(binding.productImageView)

        binding.productNameTextView.text = product.name
        binding.productConditionTextView.text = product.property?.find { it.id == 11L  }?.name
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
            presenter.createCdekOrder(args.order)
        }
    }

    override fun setProfileInfo(user: User){
        Glide.with(this)
            .load(user.avatar?.photo)
            .into(binding.sellerAvatarImageView)

    }

    override fun loading(loading: Boolean) {

    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun success() {
        showMessage("заказ создан")
        findNavController().popBackStack()
    }
}