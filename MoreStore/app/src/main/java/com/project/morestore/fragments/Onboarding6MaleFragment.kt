package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOnboarding6Binding
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.presenters.OnboardingPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class Onboarding6MaleFragment : MvpAppCompatFragment(R.layout.fragment_onboarding6), OnBoardingMvpView {
    private val binding: FragmentOnboarding6Binding by viewBinding()
    @Inject
    lateinit var onboardingPresenter: OnboardingPresenter
    private val presenter by moxyPresenter { onboardingPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        saveOnBoardingViewed()

    }

    private fun setClickListeners() {
        binding.createAccountBtn.setOnClickListener {
            findNavController().navigate(
                Onboarding6MaleFragmentDirections.actionOnboarding6MaleFragmentToMainFragment()
            )
        }
        binding.guestLoginBtn.setOnClickListener {
            presenter.changeToGuestMode()
            findNavController().navigate(Onboarding6MaleFragmentDirections.actionOnboarding6MaleFragmentToMainFragment())
        }
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
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