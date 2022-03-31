package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeCardsAdapter
import com.project.morestore.databinding.FragmentOnboarding2Binding
import com.project.morestore.models.Property
import com.project.morestore.models.Size
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.presenters.OnboardingPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Onboarding2Fragment : MvpAppCompatFragment(R.layout.fragment_onboarding2), OnBoardingMvpView {
    private val binding: FragmentOnboarding2Binding by viewBinding()
    private val args: Onboarding2FragmentArgs by navArgs()
    private val presenter by moxyPresenter { OnboardingPresenter(requireContext()) }
    private var topSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var bottomSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var shoesSizeCardAdapter: SizeCardsAdapter by autoCleared()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLists()
        setClickListeners()
        getAllSizes()
    }


    private fun initLists() {
        topSizeCardAdapter = SizeCardsAdapter(true)
        bottomSizeCardAdapter = SizeCardsAdapter(true)
        shoesSizeCardAdapter = SizeCardsAdapter(true)
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
           // presenter.saveSizes(
             //   topSizeCardAdapter.getChosenSizes(),
               // bottomSizeCardAdapter.getChosenSizes(),
               // shoesSizeCardAdapter.getChosenSizes()
           // )

         presenter.saveSizes(
             topSizeCardAdapter.getSizes(),
             bottomSizeCardAdapter.getSizes(),
             shoesSizeCardAdapter.getSizes()
         )
        }
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun showLoading(loading: Boolean){
        binding.continueBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }

    private fun getAllSizes(){
        presenter.getTopSizes()
        presenter.getBottomSizes()
        presenter.getShoosSizes()
    }

    override fun loading() {
        showLoading(true)
    }

    override fun loaded(result: List<Any>) {
        showLoading(false)
        if((result[0] as Property).idCategory?.toInt() == 4)
        topSizeCardAdapter.updateList((result as List<Property>).map { Size(it.id.toInt(), it.name, it.idCategory?.toInt(), false) })

        if((result[0] as Property).idCategory?.toInt() == 5)
            bottomSizeCardAdapter.updateList((result as List<Property>).map { Size(it.id.toInt(), it.name, it.idCategory?.toInt(), false) })

        if((result[0] as Property).idCategory?.toInt() == 6)
            shoesSizeCardAdapter.updateList((result as List<Property>).map { Size(it.id.toInt(), it.name, it.idCategory?.toInt(), false) })

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