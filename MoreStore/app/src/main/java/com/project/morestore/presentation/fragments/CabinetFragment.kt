package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCabinetBinding
import com.project.morestore.domain.presenters.CabinetPresenter
import com.project.morestore.domain.presenters.SalesPresenter
import com.project.morestore.presentation.mvpviews.CabinetMvpView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

class CabinetFragment: MvpAppCompatFragment(R.layout.fragment_cabinet), CabinetMvpView {
    val binding: FragmentCabinetBinding by viewBinding()
    @Inject
    lateinit var cabinetPresenter: CabinetPresenter
    private val presenter by moxyPresenter { cabinetPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        presenter.getSalesCount()
        presenter.getOrdersCount()
    }

    fun setClickListeners(){
        binding.inProcessSalesTextView.setOnClickListener{
               findNavController().navigate(R.id.salesActiveFragment)
        }
        binding.finishedSalesTextView.setOnClickListener{
            findNavController().navigate(R.id.salesHistoryFragment)
        }

        binding.inProcessOrdersTextView.setOnClickListener{
            findNavController().navigate(R.id.ordersActiveFragment)
        }

        binding.finishedOrdersTextView.setOnClickListener {
            findNavController().navigate(R.id.ordersHistoryFragment)
        }
    }

    override fun showSalesCount(activeSalesCount: Int, finishedSalesCount: Int) {
        binding.inProcessSalesCountTextView.text = activeSalesCount.toString()
        binding.finishedSalesCountTextView.text = finishedSalesCount.toString()
    }

    override fun showOrdersCount(activeOrdersCount: Int, finishedOrdersCount: Int) {
        binding.inProcessOrdersCountTextView.text = activeOrdersCount.toString()
        binding.finishedOrdersCountTextView.text = finishedOrdersCount.toString()
    }


}