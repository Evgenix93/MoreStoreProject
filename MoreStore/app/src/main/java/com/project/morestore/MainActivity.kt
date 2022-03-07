package com.project.morestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.InputDevice
import android.view.Window
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.databinding.ActivityMainBinding
import com.project.morestore.fragments.SplashScreenFragmentDirections


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
        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent){
        Log.d("error", "handleIntent")
        intent.data?.let {
            findNavController(R.id.fragmentContainerView).navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToProductDetailsFragment(null, it.lastPathSegment.orEmpty(), false))
        }
    }

    private fun changeStatusBarColor(colorRes: Int) {
        window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            setStatusBarColor(resources.getColor(colorRes))
        }
    }

    fun showBottomNavBar(show: Boolean) {
        binding.bottomNavBar.isVisible = show
    }

    private fun initNavController() {
        findNavController(R.id.fragmentContainerView).addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.onboarding1Fragment) {
                changeStatusBarColor(R.color.gray7)
            } else {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    changeStatusBarColor(R.color.white)
                } else {
                    changeStatusBarColor(R.color.green)
                }
            }

            val bottomNavBar = binding.bottomNavBar


            when (destination.id) {
                R.id.catalogFragment -> {
                    bottomNavBar.selectedItemId = R.id.catalogFragment
                    bottomNavBar.menu.findItem(R.id.mainFragment).icon =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_house2,
                            null
                        )
                    bottomNavBar.menu.findItem(R.id.catalogFragment).icon =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_tshirt2,
                            null
                        )
                    bottomNavBar.menu.findItem(R.id.createProductStep1Fragment).icon =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_create_product,
                            null
                        )
                    bottomNavBar.menu.findItem(R.id.messagesFragment).icon =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_chat,
                            null
                        )
                    bottomNavBar.menu.findItem(R.id.cabinetFragment).icon =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_user_circle,
                            null
                        )

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                }

                R.id.mainFragment -> {
                    bottomNavBar.selectedItemId = R.id.mainFragment
                    bottomNavBar.menu.findItem(R.id.mainFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_house, null)
                    bottomNavBar.menu.findItem(R.id.catalogFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_tshirt, null)
                    bottomNavBar.menu.findItem(R.id.createProductStep1Fragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_create_product, null)
                    bottomNavBar.menu.findItem(R.id.messagesFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_chat, null)
                    bottomNavBar.menu.findItem(R.id.cabinetFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_user_circle, null)

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                }

                R.id.createProductStep1Fragment -> {
                    bottomNavBar.selectedItemId = R.id.createFragment
                    bottomNavBar.menu.findItem(R.id.mainFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_house2, null)
                    bottomNavBar.menu.findItem(R.id.catalogFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_tshirt, null)
                    bottomNavBar.menu.findItem(R.id.createProductStep1Fragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_plus2, null)
                    bottomNavBar.menu.findItem(R.id.messagesFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_chat, null)
                    bottomNavBar.menu.findItem(R.id.cabinetFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_user_circle, null)

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

                }

                R.id.messagesFragment -> {
                    bottomNavBar.selectedItemId = R.id.messagesFragment
                    bottomNavBar.menu.findItem(R.id.mainFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_house2, null)
                    bottomNavBar.menu.findItem(R.id.catalogFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_tshirt, null)
                    bottomNavBar.menu.findItem(R.id.createProductStep1Fragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_create_product, null)
                    bottomNavBar.menu.findItem(R.id.messagesFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_chat2, null)
                    bottomNavBar.menu.findItem(R.id.cabinetFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_user_circle, null)

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                }

                R.id.cabinetFragment -> {
                    bottomNavBar.selectedItemId = R.id.cabinetFragment
                    bottomNavBar.menu.findItem(R.id.mainFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_house2, null)
                    bottomNavBar.menu.findItem(R.id.catalogFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_tshirt, null)
                    bottomNavBar.menu.findItem(R.id.createProductStep1Fragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_create_product, null)
                    bottomNavBar.menu.findItem(R.id.messagesFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_chat, null)
                    bottomNavBar.menu.findItem(R.id.cabinetFragment).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_user_circle2, null)

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                }

            }
        }
    }

    private fun initBottomNavBar() {
        val bottomNavBar = binding.bottomNavBar
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.createProductStep1Fragment -> if (navController.currentDestination?.id != R.id.createFragment) {
                    navController.navigate(R.id.createProductStep1Fragment)
                    true
                } else {
                    true
                }

                R.id.mainFragment -> if (navController.currentDestination?.id != R.id.mainFragment) {
                    navController.navigate(R.id.mainFragment)
                    true
                } else {
                    true
                }

                R.id.catalogFragment -> if (navController.currentDestination?.id != R.id.catalogFragment) {
                    navController.navigate(R.id.catalogFragment, null)
                    true
                } else {
                    true
                }

                R.id.messagesFragment -> if (navController.currentDestination?.id != R.id.messagesFragment) {
                    navController.navigate(R.id.messagesFragment)
                    true
                } else {
                    true
                }

                R.id.cabinetFragment -> if (navController.currentDestination?.id != R.id.cabinetFragment) {
                    navController.navigate(R.id.cabinetFragment)
                    true
                } else {
                    true
                }
                else -> true


            }
        }


    }

    fun changeSoftInputMode(mode: Int){
        window.setSoftInputMode(mode)
    }
}

