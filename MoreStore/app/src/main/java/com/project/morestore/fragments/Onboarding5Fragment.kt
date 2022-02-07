package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOnboarding5Binding
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.presenters.OnboardingPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Onboarding5Fragment: MvpAppCompatFragment(R.layout.fragment_onboarding5), OnBoardingMvpView {
    private val binding: FragmentOnboarding5Binding by viewBinding()
    private val args: Onboarding5FragmentArgs by navArgs()
    private val presenter  by  moxyPresenter { OnboardingPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()

    }

    private fun setClickListeners(){
        binding.continueBtn.setOnClickListener{
            presenter.safeFilter()
        }

        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    override fun loading() {
        TODO("Not yet implemented")
    }

    override fun loaded(result: List<Any>) {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
        TODO("Not yet implemented")
    }

    override fun success() {
        if (args.isMale)
            findNavController().navigate(
                Onboarding5FragmentDirections.actionOnboarding5FragmentToOnboarding6MaleFragment()
            )
        else
            findNavController().navigate(
                Onboarding5FragmentDirections.actionOnboarding5FragmentToOnboarding6FemaleFragment()
            )
    }
}