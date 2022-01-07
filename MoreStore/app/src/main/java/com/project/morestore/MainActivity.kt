package com.project.morestore

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




    }

    override fun onStart() {
        super.onStart()
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

    private fun changeStatusBarColor(colorRes: Int){
        window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            setStatusBarColor(resources.getColor(colorRes))
        }
    }



}