package com.project.morestore

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Insets
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.project.morestore.databinding.ActivityMainBinding
import com.project.morestore.fragments.SellerProfileFragment
import com.project.morestore.fragments.SplashScreenFragmentDirections
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.MessagingService
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter


class MainActivity : MvpAppCompatActivity(), MainMvpView {
    private val binding: ActivityMainBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(this) }
    private var isMessagesUnread = false
    private val messageReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            //isMessagesUnread = true
            //showUnreadMessagesIcon(isMessagesUnread)
            presenter.showUnreadMessages()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkGooglePlayServices()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
        IntentFilter(MessagingService.MESSAGE_INTENT)
        )
    }

    override fun onStart() {
        super.onStart()
        initNavController()
        initBottomNavBar()
        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent){
        if(intent.data?.path?.contains("products") == true)
        intent.data?.let {
            findNavController(R.id.fragmentContainerView).navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToProductDetailsFragment(null, it.lastPathSegment.orEmpty(), false))
        }else if(intent.data?.path?.contains("users") == true)
            intent.data?.let{
                findNavController(R.id.fragmentContainerView).navigate(R.id.sellerProfileFragment, bundleOf(
                    SellerProfileFragment.USER_ID to it.lastPathSegment?.toLong()))
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
        if(show)
            binding.newMessagesIcon.isVisible = isMessagesUnread
        else
            binding.newMessagesIcon.isVisible = false
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

                   // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

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

                  //  window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

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

                 //   window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

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

                //    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

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

                 //   window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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

    fun hideBottomIndication(){
        with(binding.bottomNavBar){
            menu.findItem(R.id.mainFragment).icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_house2, null)
            menu.findItem(R.id.catalogFragment).icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_tshirt, null)
            menu.findItem(R.id.createProductStep1Fragment).icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_create_product, null)
            menu.findItem(R.id.messagesFragment).icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_chat, null)
            menu.findItem(R.id.cabinetFragment).icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_user_circle, null)
        }
    }

    fun changeSoftInputMode(mode: Int) {
            window.setSoftInputMode(mode)
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.fragmentContainerView)
        when (navController.previousBackStackEntry?.destination?.id) {
            R.id.mainFragment -> if(navController.currentDestination?.id
                == R.id.registration3Fragment) navController.navigate(R.id.firstLaunchFragment, null,
            NavOptions.Builder().setPopUpTo(R.id.splashScreenFragment, false).build())
            else super.onBackPressed()
            R.id.createProductStep6Fragment -> navController.navigate(R.id.catalogFragment, null,
            NavOptions.Builder().setPopUpTo(R.id.mainFragment, false).build())
            else -> super.onBackPressed()
        }
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun checkGooglePlayServices(): Boolean {
        // 1
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        // 2
        return if (status != ConnectionResult.SUCCESS) {
            Log.e("googlePlay", "Error")
            // ask user to update google play services and manage the error.
            false
        } else {
            // 3
            Log.i("googlePlay", "Google play services updated")
            true
        }
    }

    fun showUnreadMessagesIcon(show: Boolean){
        if(binding.bottomNavBar.isVisible)
        binding.newMessagesIcon.isVisible = show
        isMessagesUnread = show
    }

    override fun loaded(result: Any) {
        val isUnread = result as Boolean
        showUnreadMessagesIcon(isUnread)

    }

    override fun loading() {

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun loginFailed() {

    }

    override fun success() {

    }
}

