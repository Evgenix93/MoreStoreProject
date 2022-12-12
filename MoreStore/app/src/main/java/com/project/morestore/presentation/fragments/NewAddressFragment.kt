package com.project.morestore.presentation.fragments

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
import com.project.morestore.R
import com.project.morestore.databinding.FragmentMyaddressNewBinding
import com.project.morestore.presentation.dialogs.AddressDeleteDialog
import com.project.morestore.presentation.fragments.CitiesFragment.Companion.REQUEST_KEY
import com.project.morestore.presentation.fragments.CitiesFragment.Companion.SINGLE
import com.project.morestore.presentation.fragments.CitiesFragment.Companion.TYPE
import com.project.morestore.presentation.fragments.MapMarkerAddressesFragment.Companion.CITY
import com.project.morestore.presentation.fragments.MapMarkerAddressesFragment.Companion.HOUSE
import com.project.morestore.presentation.fragments.MapMarkerAddressesFragment.Companion.INDEX
import com.project.morestore.presentation.fragments.MapMarkerAddressesFragment.Companion.STREET
import com.project.morestore.data.models.MyAddress
import com.project.morestore.presentation.mvpviews.NewAddressView
import com.project.morestore.domain.presenters.NewAddressPresenter
import com.project.morestore.util.addTextChangeListener
import com.project.morestore.util.attachNavigation
import com.project.morestore.util.setPhoneField
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class NewAddressFragment :MvpAppCompatFragment(), NewAddressView {
    companion object { const val EDIT_ADDRESS = "edit_address" }
    private lateinit var views : FragmentMyaddressNewBinding
    @Inject
    lateinit var newAddressPresenter: NewAddressPresenter
    private val presenter :NewAddressPresenter by moxyPresenter {
       newAddressPresenter
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
            if(granted) presenter.findCity()
            else notFoundCity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMyaddressNewBinding.inflate(inflater).also{ views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setInfo(arguments?.getParcelable(EDIT_ADDRESS))

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

            listOf(fullname, phoneNumber, street, index, house, housing, building, apartment)
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
                presenter.save(defaultAddress.isChecked, arguments?.getParcelable(EDIT_ADDRESS))
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

    override fun requestCity() {
        permissionLauncher.launch(ACCESS_FINE_LOCATION)
    }

    override fun showFullname(fullname :String) {
        if(views.fullname.text.isEmpty())
           views.fullname.setText(fullname)
    }

    override fun showPhone(phone :String) {
        if(views.phoneNumber.text.toString() == "+7 (")
           views.phoneNumber.setText(phone)
    }

    override fun showFavorite(isFavorite: Boolean) {
        views.defaultAddress.apply{
            isChecked = isFavorite
            jumpDrawablesToCurrentState()
        }
    }

    override fun showCity(city :String) {
        if(views.city.text != "Выберите город") {
            presenter.city = views.city.text.toString()
            return
        }
        views.city.text = city
        views.city.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        views.city.setBackgroundResource(R.drawable.bg_rect_borddark_round4)
    }

    override fun notFoundCity() {
        if(views.city.text != "Выберите город") {
            presenter.city = views.city.text.toString()
            return
        }
        views.city.setText(R.string.myAddress_new_address_city_notFound)
        views.city.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))
        views.city.setBackgroundResource(R.drawable.bg_rect_bordlight_round4)
    }

    override fun validForm(valid: Boolean) {
        views.save.isEnabled = valid
    }

    override fun confirmDelete() {
        AddressDeleteDialog(requireContext()){ presenter.confirmDelete(requireArguments().getParcelable(EDIT_ADDRESS)) }.show()
    }

    override fun back() {
        findNavController().popBackStack()
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}