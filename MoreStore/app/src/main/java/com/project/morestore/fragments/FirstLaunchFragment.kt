package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.LoginDialog
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFirstLaunchBinding
import com.project.morestore.models.PropertyType
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.singletones.Network
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FirstLaunchFragment: MvpAppCompatFragment(R.layout.fragment_first_launch), MainMvpView {
    private val binding: FragmentFirstLaunchBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavBar()
        setClickListeners()
        //checkToken()
    }

    private fun checkToken(){
        presenter.checkToken()

    }


    private fun setClickListeners(){
        binding.createAccountBtn.setOnClickListener {
            findNavController().navigate(FirstLaunchFragmentDirections.actionFirstLaunchFragmentToLoginDialog())

        }

        binding.guestLoginBtn.setOnClickListener{
            findNavController().navigate(FirstLaunchFragmentDirections.actionFirstLaunchFragmentToMainFragment())
        }
    }

   private fun hideBottomNavBar(){
       val mainActivity = activity as MainActivity
       mainActivity.showBottomNavBar(false)
   }

    override fun loaded(result: Any) {

        //if(result is Boolean){
          //  if(result)
           //     findNavController().navigate(FirstLaunchFragmentDirections.actionFirstLaunchFragmentToMainFragment())
      //  }

    }

    override fun loading() {

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>) {

    }

    override fun success() {

    }

}