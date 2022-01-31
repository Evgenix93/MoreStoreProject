package com.project.morestore.fragments

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.location.LocationServices
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentAutoregionBinding
import com.project.morestore.models.Address
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class AutoLocationFragment: MvpAppCompatFragment(R.layout.fragment_autoregion), UserMvpView {
    private val binding: FragmentAutoregionBinding by viewBinding()
    private lateinit var permissionsLauncher: ActivityResultLauncher<String>
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        showBottomNav()
        initPermissionsLauncher()
        getPermissions()
    }

    private fun initToolbar(){
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun showBottomNav(){
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun initPermissionsLauncher(){
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
            if(granted){
                getCity()
            }
        }

    }

    private fun getPermissions(){
        permissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun getCity(){
        try {
            val locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
            locationProvider.lastLocation.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.d("mylog", task.result.longitude.toString())
                    presenter.getCityByCoordinates("${task.result.latitude},${task.result.longitude}")
                }else{
                    Toast.makeText(requireContext(), "ошибка", Toast.LENGTH_SHORT).show()
                    binding.autoLocationResultTextView.text = "К сожалению нам не удалось определить\nваш населённый пункт"
                }

            }

        }catch (e: SecurityException){

        }
    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }

    override fun success(result: Any) {
        showLoading(false)

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        binding.autoLocationResultTextView.text = "К сожалению нам не удалось определить\nваш населённый пункт"

    }

    override fun loading() {
        showLoading(true)


    }

    override fun loaded(result: Any) {
        showLoading(false)
        binding.autoLocationResultTextView.text = (result as Address).fullCity.name
    }

    override fun successNewCode() {

    }

}