package com.project.morestore.presentation.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentAutoregionBinding
import com.project.morestore.data.models.Address
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class AutoLocationFragment: MvpAppCompatFragment(R.layout.fragment_autoregion), UserMvpView {
    private val binding: FragmentAutoregionBinding by viewBinding()
    private lateinit var permissionsLauncher: ActivityResultLauncher<String>
    @Inject lateinit var userPresenter: UserPresenter
    private val presenter by moxyPresenter { userPresenter }
    private val args: AutoLocationFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        showBottomNav()
        getFilter()
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
            showLoading(true)
            val locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
            val cancelToken = object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                    return this

                }

                override fun isCancellationRequested(): Boolean {
                    return false

                }
            }
            locationProvider.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, cancelToken ).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.d("mylog", task.result.longitude.toString())
                    task.result?.let {
                        presenter.getCityByCoordinates("${it.latitude},${it.longitude}")
                    }
                    task.result ?: run {
                        showLoading(false)
                        Toast.makeText(requireContext(), "ошибка", Toast.LENGTH_SHORT).show()
                        binding.autoLocationResultTextView.text = "К сожалению нам не удалось определить\nваш населённый пункт"
                    }
                }else{
                    showLoading(false)
                    Toast.makeText(requireContext(), "ошибка", Toast.LENGTH_SHORT).show()
                    binding.autoLocationResultTextView.text = "К сожалению нам не удалось определить\nваш населённый пункт"
                }

            }

        }catch (e: SecurityException){

        }
    }

    private fun getFilter(){
        presenter.getFilter()
    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }



    override fun success(result: Any) {
        showLoading(false)
        findNavController().popBackStack(R.id.catalogFragment, false)
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
        if(result is Address) {
            binding.autoLocationResultTextView.text = "Ваш город ${result.fullAddress?.substringBefore(',')}?"
            binding.yesBtn.isVisible = true
            binding.noBtn.isVisible = true
            binding.yesBtn.setOnClickListener {
                if(args.isForFilter)
                presenter.changeUserCity(result.fullAddress?.substringBefore(","))
                else
                    presenter.changeCurrentUserAddress(result)
            }
            binding.noBtn.setOnClickListener {
                if(args.isForFilter)
                findNavController().popBackStack(R.id.catalogFragment, false)
                else
                    findNavController().popBackStack(R.id.profileFragment, false)
            }
        }
    }
}