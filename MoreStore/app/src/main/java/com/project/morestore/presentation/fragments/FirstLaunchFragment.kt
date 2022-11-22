package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFirstLaunchBinding
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.data.singletones.Token
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FirstLaunchFragment: MvpAppCompatFragment(R.layout.fragment_first_launch) {
    private val binding: FragmentFirstLaunchBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavBar()
        setClickListeners()
    }


    private fun setClickListeners(){
        binding.createAccountBtn.setOnClickListener {
            findNavController().navigate(FirstLaunchFragmentDirections.actionFirstLaunchFragmentToLoginDialog())

        }

        binding.guestLoginBtn.setOnClickListener{
            Token.token = ""
            Token.userId = 0
            findNavController().navigate(FirstLaunchFragmentDirections.actionFirstLaunchFragmentToMainFragment())
        }
    }

   private fun hideBottomNavBar(){
       val mainActivity = activity as MainActivity
       mainActivity.showBottomNavBar(false)
   }



}