package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFirstLaunchBinding
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.presenters.OnboardingPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Onboarding6FemaleFragment : MvpAppCompatFragment(R.layout.fragment_first_launch), OnBoardingMvpView {
    private val binding: FragmentFirstLaunchBinding by viewBinding()
    private val presenter by moxyPresenter { OnboardingPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initText()
        setClickListeners()
        saveOnBoardingViewed()

    }

    private fun setClickListeners() {
        binding.createAccountBtn.setOnClickListener {
            findNavController().navigate(
                Onboarding6FemaleFragmentDirections.actionOnboarding6FemaleFragmentToMainFragment()
            )
        }

        binding.guestLoginBtn.setOnClickListener {
            presenter.changeToGuestMode()
            findNavController().navigate(Onboarding6FemaleFragmentDirections.actionOnboarding6FemaleFragmentToMainFragment())
        }

        binding.backIcon.isVisible = true
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun initText() {
        binding.createAccountBtn.text = "Авторизоваться"
    }

    private fun saveOnBoardingViewed(){
        presenter.saveOnBoardingViewed()
    }

    override fun loading() {

    }

    override fun loaded(result: List<Any>) {

    }

    override fun error(message: String) {

    }

    override fun success() {

    }

}