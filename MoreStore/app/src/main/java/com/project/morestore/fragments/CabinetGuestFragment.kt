package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCabinetGuestBinding

class CabinetGuestFragment: Fragment(R.layout.fragment_cabinet_guest) {
    private val binding: FragmentCabinetGuestBinding by viewBinding()
    private val args: CabinetGuestFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        showBottomNav()
        initToolbar()
        initLoginTextView()
    }



    private fun setClickListeners(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(CabinetGuestFragmentDirections.actionCabinetGuestFragmentToLoginDialog())
        }
    }

    private fun showBottomNav(){
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun initLoginTextView(){
      if(args.fromCreateProduct){
          binding.needLoginTextView.text = getString(R.string.need_login2)
      }
        if(findNavController().previousBackStackEntry?.destination?.id == R.id.productDetailsFragment)
            binding.needLoginTextView.text = "Для входа в чат\nнеобходимо зарегистрироваться"

        if(findNavController().previousBackStackEntry?.destination?.id == R.id.messagesFragment)
            binding.needLoginTextView.text = "Для входа в сообщения\nнеобходимо зарегистрироваться"

    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().navigate(R.id.firstLaunchFragment)
        }
        if(args.fromCreateProduct)
            binding.toolbar.titleTextView.text = getString(R.string.create_product)
        else
            binding.toolbar.titleTextView.text = getString(R.string.cabinet)

        if(findNavController().previousBackStackEntry?.destination?.id == R.id.productDetailsFragment)
            binding.toolbar.titleTextView.text = "Чат"

        if(findNavController().previousBackStackEntry?.destination?.id == R.id.messagesFragment)
            binding.toolbar.titleTextView.text = "Сообщения"






    }
}