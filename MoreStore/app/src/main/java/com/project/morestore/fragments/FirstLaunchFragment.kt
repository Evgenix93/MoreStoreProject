package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFirstLaunchBinding
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.singletones.Token
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FirstLaunchFragment: MvpAppCompatFragment(R.layout.fragment_first_launch), MainMvpView {
    private val binding: FragmentFirstLaunchBinding by viewBinding()
    @Inject lateinit var mainPresenter: MainPresenter
    private val presenter by moxyPresenter { mainPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavBar()
        setClickListeners()
    }

    private fun checkToken(){
        presenter.checkToken()

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

    override fun loaded(result: Any) {

    }

    override fun loading() {

    }

    override fun error(message: String) {

    }

    override fun success() {

    }

}