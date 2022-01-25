package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCabinetBinding
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CabinetFragment: MvpAppCompatFragment(R.layout.fragment_cabinet), UserMvpView {
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private val binding: FragmentCabinetBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkToken()
        setClickListeners()
    }

    private fun checkToken(){
        presenter.checkToken()
    }

    private fun setClickListeners(){
        binding.logoutBtn.setOnClickListener{
            presenter.clearToken()
        }
    }

    override fun success(result: Any) {
        findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToFirstLaunchFragment3())
    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        val tokenIsEmpty = result as Boolean
        if (tokenIsEmpty) {
            binding.logoutBtn.isVisible = false
            findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToLoginDialog())
        }
    }

    override fun successNewCode() {

    }
}