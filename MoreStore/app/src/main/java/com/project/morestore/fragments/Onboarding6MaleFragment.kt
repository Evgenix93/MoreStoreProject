package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOnboarding6Binding
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.presenters.ProductPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Onboarding6MaleFragment : MvpAppCompatFragment(R.layout.fragment_onboarding6), OnBoardingMvpView {
    private val binding: FragmentOnboarding6Binding by viewBinding()
    private val presenter by moxyPresenter { ProductPresenter(requireContext()) }

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