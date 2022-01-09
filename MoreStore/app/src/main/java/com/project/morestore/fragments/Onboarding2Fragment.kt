package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeCardsAdapter
import com.project.morestore.databinding.FragmentOnboarding2Binding
import com.project.morestore.models.Size
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.presenters.ProductPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Onboarding2Fragment : MvpAppCompatFragment(R.layout.fragment_onboarding2), OnBoardingMvpView {
    private val binding: FragmentOnboarding2Binding by viewBinding()
    private val args: Onboarding2FragmentArgs by navArgs()
    private val presenter by moxyPresenter { ProductPresenter(requireContext()) }
    private var topSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var bottomSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var shoesSizeCardAdapter: SizeCardsAdapter by autoCleared()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLists()
        setClickListeners()
        presenter.getAllSizes()

    }


    private fun initLists() {
        topSizeCardAdapter = SizeCardsAdapter()
        bottomSizeCardAdapter = SizeCardsAdapter()
        shoesSizeCardAdapter = SizeCardsAdapter()
        with(binding.topSizeCardsList) {
            adapter = topSizeCardAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)

        }
        with(binding.bottomSizeCardsList) {
            adapter = bottomSizeCardAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)

        }
        with(binding.shoesSizeCardsList) {
            adapter = shoesSizeCardAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)

        }
    }

    private fun setClickListeners(){
        binding.continueBtn.setOnClickListener {
            presenter.saveSizes(
                topSizeCardAdapter.getChosenSizes(),
                bottomSizeCardAdapter.getChosenSizes(),
                shoesSizeCardAdapter.getChosenSizes()
            )


        }
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun showLoading(loading: Boolean){
        binding.continueBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }



    override fun loading() {
        showLoading(true)

    }

    override fun loaded(result: List<Any>) {
        showLoading(false)
        topSizeCardAdapter.updateList((result as List<Size>).filter { it.id_category == 1 }.sortedBy { it.toInt() })
        bottomSizeCardAdapter.updateList((result).filter { it.id_category == 2 }.sortedBy { it.toInt() })
        shoesSizeCardAdapter.updateList((result).filter { it.id_category == 3 }
            .sortedBy { it.name.toFloat() })



    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun success() {
        showLoading(false)
        findNavController().navigate(
            Onboarding2FragmentDirections.actionOnboarding2FragmentToOnboarding3Fragment(
                args.isMale
            )
        )
    }
}