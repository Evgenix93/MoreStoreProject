package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var sellersAdapter: SellersAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSellersRecyclerView()
        getWaitReviewSellers()
    }

    private fun getWaitReviewSellers(){
        presenter.getWaitReviewSellers()
    }

    private fun initSellersRecyclerView(){
        with(binding.sellersRecyclerView){
            adapter = SellersAdapter{
                findNavController().navigate(WaitReviewSellersFragmentDirections
                    .actionWaitReviewSellersFragmentToSellerProfileFragment(it, false))
            }.also{sellersAdapter = it}
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initToolbar(){
        binding.toolbar.apply { titleTextView.text = "Ждут оценки"
        actionTextView.isVisible = false
         arrowBackImageView.setOnClickListener{findNavController().popBackStack()}
        }
    }

    override fun onSellersLoaded(sellers: List<User>) {
        emptyList(false)
        sellersAdapter.updateList(sellers)
    }

    override fun onError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        emptyList(true)
    }

    private fun emptyList(isEmpty: Boolean){
        binding.emptyListImageView.isVisible = isEmpty
        binding.emptyListTextView.isVisible = isEmpty
        binding.sellersRecyclerView.isVisible = isEmpty.not()
    }

}