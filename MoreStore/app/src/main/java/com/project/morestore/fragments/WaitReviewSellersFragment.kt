package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SellersAdapter
import com.project.morestore.databinding.FragmentWaitReviewBinding
import com.project.morestore.models.User
import com.project.morestore.mvpviews.WaitReviewSellersMvpView
import com.project.morestore.presenters.ReviewsPresenter
import com.project.morestore.presenters.WaitReviewSellersPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class WaitReviewSellersFragment: MvpAppCompatFragment(R.layout.fragment_wait_review), WaitReviewSellersMvpView {
     private val binding: FragmentWaitReviewBinding by viewBinding()
     private val presenter: WaitReviewSellersPresenter by moxyPresenter { WaitReviewSellersPresenter(requireContext()) }
    private val sellersAdapter: SellersAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getWaitReviewSellers()
    }

    private fun getWaitReviewSellers(){
        presenter.getWaitReviewSellers()
    }

    private fun updateSellersList(newList: List<User>){

    }

    override fun onSellersLoaded(sellers: List<User>) {

    }

    override fun onError(message: String) {
        TODO("Not yet implemented")
    }

}