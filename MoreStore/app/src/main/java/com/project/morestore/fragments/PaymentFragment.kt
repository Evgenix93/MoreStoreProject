package com.project.morestore.fragments

import androidx.navigation.fragment.navArgs
import com.project.morestore.R
import com.project.morestore.presenters.RaiseProductPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class PaymentFragment: MvpAppCompatFragment(R.layout.fragment_social_login) {
    private val presenter by moxyPresenter { RaiseProductPresenter(requireContext()) }
    private val args: RaiseProductFragmentArgs by navArgs()
}