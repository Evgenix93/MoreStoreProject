package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentSuccessPromoteBinding

class SuccessOrderPaymentFragment: Fragment(R.layout.fragment_success_promote) {
    private val binding: FragmentSuccessPromoteBinding by viewBinding()
    private val args: SuccessOrderPaymentFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.successInfoTextView.setText(R.string.success_order_payment)
        binding.timePeriodInfoTextView.setText(R.string.success_order_payment_info)
        binding.finishBtn.setText(R.string.show_order_status_btn)
        binding.finishBtn.setOnClickListener {
            findNavController().navigate(
                SuccessOrderPaymentFragmentDirections
                    .actionSuccessOrderPaymentFragmentToOrderDetailsFragment(orderId = args.orderId)
            )
        }

    }
}