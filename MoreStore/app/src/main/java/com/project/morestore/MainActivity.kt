package com.project.morestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        initNavController()
        initBottomNavBar()
    }

    private fun changeStatusBarColor(colorRes: Int){
        window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            setStatusBarColor(resources.getColor(colorRes))
        }
    }

   fun showBottomNavBar(show: Boolean){
       binding.bottomNavBar.isVisible = show
   }

   private fun initNavController(){
       findNavController(R.id.fragmentContainerView).addOnDestinationChangedListener { controller, destination, arguments ->
           if(destination.id == R.id.onboarding1Fragment){
               changeStatusBarColor(R.color.gray7)
           }else{
               if(android.os.Build.VERSION.SDK_INT >= 23) {
                   changeStatusBarColor(R.color.white)
               }else{
                   changeStatusBarColor(R.color.green)
               }
           }

       }
   }

    private fun initBottomNavBar(){
        binding.bottomNavBar.setupWithNavController(findNavController(R.id.fragmentContainerView))
    }

}