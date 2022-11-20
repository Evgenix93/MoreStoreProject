package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOnboarding5Binding
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.presenters.OnboardingPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class Onboarding5Fragment: MvpAppCompatFragment(R.layout.fragment_onboarding5), OnBoardingMvpView {
    private val binding: FragmentOnboarding5Binding by viewBinding()
    private val args: Onboarding5FragmentArgs by navArgs()
    @Inject
    lateinit var onboardingPresenter: OnboardingPresenter
    private val presenter  by  moxyPresenter { onboardingPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()

    }

    private fun setClickListeners(){
        binding.continueBtn.setOnClickListener{
            presenter.saveOnboardingData(args.isMale)
        }

        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    override fun loading() {
        binding.loader.isVisible = true
        binding.continueBtn.isEnabled = false
    }

    override fun loaded(result: List<Any>) {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        binding.continueBtn.isEnabled = true
        Log.e("MyDebug", "message = $message")
    }

    override fun success() {
        binding.loader.isVisible = false
        binding.continueBtn.isEnabled = true
            findNavController().navigate(
                Onboarding5FragmentDirections.actionOnboarding5FragmentToMainFragment()
            )
    }
}