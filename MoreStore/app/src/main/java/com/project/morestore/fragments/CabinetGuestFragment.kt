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
          when(arguments?.getInt(FRAGMENT_ID)){
              R.id.createProductStep1Fragment -> {
                  binding.needLoginTextView.text = getString(R.string.need_login2)
                  binding.toolbar.titleTextView.text = getString(R.string.create_product)
              }

                  R.id.productDetailsFragment -> {
                      binding.toolbar.titleTextView.text = "Чат"
                      binding.needLoginTextView.text = "Для входа в чат\nнеобходимо зарегистрироваться"
                  }
                  R.id.messagesFragment -> {
                      binding.toolbar.titleTextView.text = "Сообщения"
                      binding.needLoginTextView.text = "Для входа в сообщения\nнеобходимо зарегистрироваться"
                  }
                  R.id.ordersCartFragment -> {
                      binding.toolbar.titleTextView.text = "Корзина"
                      binding.needLoginTextView.text = "Для входа в корзину\nнеобходимо зарегистрироваться"
                  }
                  R.id.favoritesFragment -> {
                      binding.toolbar.titleTextView.text = "Избранное"
                      binding.needLoginTextView.text = "Для входа в избранное\nнеобходимо зарегистрироваться"
                  }
              }
          }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.titleTextView.text = "Кабинет"
        if(findNavController().previousBackStackEntry?.destination?.id == R.id.productDetailsFragment)
            binding.toolbar.titleTextView.text = "Чат"

        if(findNavController().previousBackStackEntry?.destination?.id == R.id.messagesFragment)
            binding.toolbar.titleTextView.text = "Сообщения"
    }
    companion object{
        const val FRAGMENT_ID = "fragment id"
    }
}