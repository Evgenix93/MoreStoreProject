package com.project.morestore.fragments

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.project.morestore.Geolocator
import com.project.morestore.R
import com.project.morestore.databinding.FragmentMyaddressNewBinding
import com.project.morestore.dialogs.AddressDeleteDialog
import com.project.morestore.fragments.CitiesFragment.Companion.REQUEST_KEY
import com.project.morestore.fragments.CitiesFragment.Companion.SINGLE
import com.project.morestore.fragments.CitiesFragment.Companion.TYPE
import com.project.morestore.fragments.MapMarkerAddressesFragment.Companion.CITY
import com.project.morestore.fragments.MapMarkerAddressesFragment.Companion.HOUSE
import com.project.morestore.fragments.MapMarkerAddressesFragment.Companion.INDEX
import com.project.morestore.fragments.MapMarkerAddressesFragment.Companion.STREET
import com.project.morestore.models.MyAddress
import com.project.morestore.mvpviews.NewAddressView
import com.project.morestore.presenters.NewAddressPresenter
import com.project.morestore.repositories.AddressesRepository
import com.project.morestore.repositories.GeoRepository
import com.project.morestore.repositories.UserNetworkGateway
import com.project.morestore.util.addTextChangeListener
import com.project.morestore.util.attachNavigation
import com.project.morestore.util.setPhoneField
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class NewAddressFragment :MvpAppCompatFragment(), NewAddressView {
    companion object { const val EDIT_ADDRESS = "edit_address" }
    private lateinit var views : FragmentMyaddressNewBinding
    private val presenter :NewAddressPresenter by moxyPresenter {
        NewAddressPresenter(
            Geolocator(requireContext()),
            GeoRepository(),
            UserNetworkGateway(),
            AddressesRepository,
            arguments?.getParcelable(EDIT_ADDRESS)
        )
    }
    //todo create delegate
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
            if(granted) presenter.findCity()
            else notFoundCity()//todo call from presenter after delegate
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMyaddressNewBinding.inflate(inflater).also{ views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(views.toolbar){
            root.attachNavigation()
            title.setText(R.string.myAddress_new_title)
        }
        with(views){
            fullname.addTextChangeListener { presenter.fullname = it }
            phoneNumber.addTextChangeListener { presenter.phone = it }
            street.addTextChangeListener { presenter.street = it }
            index.addTextChangeListener { presenter.index = it }
            house.addTextChangeListener { presenter.house = it }
            building.addTextChangeListener { presenter.building = it }
            housing.addTextChangeListener { presenter.housing = it }
            apartment.addTextChangeListener { presenter.apartment = it }

            arrayOf(fullname, phoneNumber, street, index, house, housing, building, apartment)
                .forEach { field ->
                    field.addTextChangeListener {
                        field.setBackgroundResource(
                            if(it.isEmpty()) R.drawable.bg_rect_bordlight_round4
                            else R.drawable.bg_rect_borddark_round4
                        )
                    }
                }
            phoneNumber.apply{ setText("+7 (") }.setPhoneField()
            selectOnMap.setOnClickListener {
                setFragmentResultListener(MapMarkerAddressesFragment.KEY){_, bundle ->
                    bundle.getString(CITY)?.let {
                        presenter.city = it
                        showCity(it)
                    }
                    bundle.getString(STREET)?.let { views.street.setText(it) }
                    bundle.getString(INDEX)?.let { views.index.setText(it) }
                    bundle.getString(HOUSE)?.let { views.house.setText(it) }
                }
                findNavController().navigate(R.id.mapMarkerAddressesFragment)
            }
            city.setOnClickListener {
                setFragmentResultListener(REQUEST_KEY) { _, bundle ->
                    views.city.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    views.city.setBackgroundResource(R.drawable.bg_rect_borddark_round4)
                    bundle.getParcelable<ParcelRegion>(SINGLE)!!.entity.name
                        .also {
                            views.city.text = it
                            presenter.city = it
                        }
                }
                findNavController().navigate(R.id.citiesFragment, bundleOf(TYPE to 0))
            }
            save.setOnClickListener {
                presenter.save(defaultAddress.isChecked)
            }
            arguments?.getParcelable<MyAddress>(EDIT_ADDRESS)?.let {
                showFullname(it.name)
                showPhone(it.phone)
                showCity(it.address.city)
                street.setText(it.address.street)
                index.setText(it.address.index)
                house.setText(it.address.house)
                building.setText(it.address.building)
                housing.setText(it.address.housing)
                apartment.setText(it.address.apartment)
                delete.visibility = VISIBLE
                delete.setOnClickListener { presenter.delete() }
            }
        }
    }

    //region Implementation View
    override fun requestCity() {
        permissionLauncher.launch(ACCESS_FINE_LOCATION)
    }

    override fun showFullname(fullname :String) {
        views.fullname.setText(fullname)
    }

    override fun showPhone(phone :String) {
        views.phoneNumber.setText(phone)
    }

    override fun showCity(city :String) {
        views.city.text = city
        views.city.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        views.city.setBackgroundResource(R.drawable.bg_rect_borddark_round4)
    }

    override fun notFoundCity() {
        views.city.setText(R.string.myAddress_new_address_city_notFound)
        views.city.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))
        views.city.setBackgroundResource(R.drawable.bg_rect_bordlight_round4)
    }

    override fun validForm(valid: Boolean) {
        views.save.isEnabled = valid
    }

    override fun confirmDelete() {
        AddressDeleteDialog(requireContext()){ presenter.confirmDelete() }.show()
    }

    override fun back() {
        findNavController().popBackStack()
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    //endregion Implementation View
}