package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOrderDetailsBinding
import com.project.morestore.models.OfferedOrderPlaceChange
import com.project.morestore.models.cart.OrderStatus
import com.project.morestore.mvpviews.OrderDetailsView
import com.project.morestore.presenters.OrderDetailsPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class OrderDetailsFragment: MvpAppCompatFragment(R.layout.fragment_order_details), OrderDetailsView {
   private val args: OrderDetailsFragmentArgs by navArgs()
    private val binding: FragmentOrderDetailsBinding by viewBinding()
    private val presenter by moxyPresenter { OrderDetailsPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAddress()
        setOrderStatus(args.orderItem.status)
    }


    private fun setAddress(){
        binding.name.text = args.orderItem.user?.name
        binding.address.text = args.orderItem.newAddress
    }

    private fun setOrderStatus(status: OrderStatus){
        when(status){
          OrderStatus.NOT_SUBMITTED_SELLER -> setNotSubmittedStatus()
          OrderStatus.MEETING_NOT_ACCEPTED_SELLER -> setMeetingNotAcceptedSellerStatus()
          OrderStatus.CHANGE_MEETING_SELLER -> setChangeMeetingSellerStatus()
          OrderStatus.RECEIVED_SELLER -> setReceivedSellerStatus()
          OrderStatus.ADD_MEETING ->  setAddMeetingStatus()
        }
    }

    private fun setNotSubmittedStatus(){
        binding.orderItemStatusBlock.isVisible = false
        binding.orderItemAcceptBlock.isVisible = true
        binding.orderItemAcceptDescription.isVisible = false
        binding.orderItemAcceptButton.text = "Подтвердить"
        binding.orderItemAcceptProblemsButton.text = "Отменить"
        binding.orderItemAcceptButton.setOnClickListener{
            presenter.submitBuy(args.orderItem.chatFunctionInfo!!, args.orderItem)
        }
        binding.orderItemAcceptProblemsButton.setOnClickListener{
            presenter.cancelBuyRequest(args.orderItem.chatFunctionInfo!!)
        }
    }

    private fun setMeetingNotAcceptedSellerStatus(){
        binding.orderItemAcceptBlock.isVisible = false
        binding.orderItemStatusContent.text = "Покупатель ещё не подтвердил место встречи"
    }

    private fun setChangeMeetingSellerStatus(){
        binding.orderItemStatusBlock.isVisible = false
        binding.orderItemAcceptDescription.isVisible = false
        binding.orderItemAcceptButton.text = "Принять место встречи"
        binding.orderItemAcceptProblemsButton.text = "Обсудить в диалоге"
        binding.orderItemAcceptButton.setOnClickListener{
            presenter.acceptOrderPlace(OfferedOrderPlaceChange(
                args.orderItem.id,
                args.orderItem.newAddressId!!,
                args.orderItem.newAddress!!,
                1
            ))
        }
    }

    private fun setReceivedSellerStatus(){
        binding.orderItemStatusBlock.isVisible = false
        binding.orderItemStatusContent.text =
            "Ожидание встречи с покупателем"
    }

    private fun setAddMeetingStatus(){
        binding.orderItemStatusBlock.isVisible = false
        binding.orderItemAcceptBlock.isVisible = true
        binding.orderItemAcceptDescription.isVisible = false
        binding.orderItemAcceptButton.text = "Добавить место встречи"
        binding.orderItemAcceptProblemsButton.isVisible = false
        binding.orderItemAcceptButton.setOnClickListener{
            presenter.addDealPlace(
                args.orderItem.id,
                args.orderItem.newAddress!!
            )
        }
    }

    override fun loading(isLoading: Boolean) {
        TODO("Not yet implemented")
    }

    override fun orderStatusChanged(status: OrderStatus) {
        setOrderStatus(status)
    }


    override fun onError(message: String) {
        TODO("Not yet implemented")
    }
}