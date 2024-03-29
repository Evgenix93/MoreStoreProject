package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.presentation.adapters.MyAddressesAdapter
import com.project.morestore.databinding.FragmentMyaddressesBinding
import com.project.morestore.presentation.dialogs.AddAddressDialog
import com.project.morestore.presentation.fragments.MapMarkerPickupsFragment.Companion.PICKUP_ADDRESS
import com.project.morestore.presentation.fragments.base.FullscreenFragment

import com.project.morestore.data.models.*
import com.project.morestore.presentation.mvpviews.MyAddressesView
import com.project.morestore.domain.presenters.MyAddressesPresenter
import com.project.morestore.util.attachNavigation
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class MyAddressesFragment :FullscreenFragment(), MyAddressesView {
    private val args: MyAddressesFragmentArgs by navArgs()
    private val adapter = MyAddressesAdapter{
        if(args.isSelectAddress){
            setFragmentResult(ADDRESS_REQUEST, bundleOf(ADDRESS_KEY to it)  )
            findNavController().navigateUp()
            return@MyAddressesAdapter
        }
        if(it.type == AddressType.CDEK.id) {
            findNavController().navigate(
                R.id.myAddressPickupFragment,
                bundleOf(MyAddressPickupFragment.EDIT_ADDRESS to it)
            )
        } else {
            findNavController().navigate(
                R.id.newAddressFragment,
                bundleOf(NewAddressFragment.EDIT_ADDRESS to it)
            )
        }
    }
    private lateinit var views : FragmentMyaddressesBinding
    @Inject lateinit var _presenter: MyAddressesPresenter
    private val presenter by moxyPresenter { _presenter }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMyaddressesBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(views.toolbar) {
            root.attachNavigation()
            title.setText(R.string.myAddress_title)
        }
        with(views){
            add.setOnClickListener {
                when (args.addressType) {
                   ADDRESSES_ALL -> AddAddressDialog {
                        if (it == AddAddressDialog.Type.PICKUP) showRegions()
                        else findNavController().navigate(R.id.newAddressFragment)
                    }.show(childFragmentManager, null)
                    ADDRESSES_HOME -> findNavController().navigate(R.id.newAddressFragment)
                    ADDRESSES_CDEK -> showRegions()
                }
            }
        }
    }

    //region View implementation
    override fun showEmpty() {
        views.content.removeAllViews()
        layoutInflater.inflate(R.layout.screen_myaddresses_empty, views.content)
    }

    override fun showAddresses(addresses: Array<MyAddress>) {
        views.content.removeAllViews()
        (layoutInflater.inflate(R.layout.screen_myaddresses, views.content, false) as RecyclerView)
            .apply {
                adapter = this@MyAddressesFragment.adapter
                setSpace(8.dp)
            }
            .also { views.content.addView(it) }
        val addressesToShow: Array<MyAddress> = when(args.addressType){
            ADDRESSES_ALL -> addresses
            ADDRESSES_HOME -> addresses.filter { it.type == AddressType.HOME.id }.toTypedArray()
            ADDRESSES_CDEK -> addresses.filter { if(args.isSeller) it.type == AddressType.CDEK.id
                                                else it.type != AddressType.HOME.id}.toTypedArray()
            else -> addresses
        }
        adapter.setItems(addressesToShow)
    }
    //endregion View implementation

    private fun showRegions(){
        setFragmentResultListener(CitiesFragment.REQUEST_KEY) { _, bundle ->
            showMap(bundle.getParcelable<ParcelRegion>(CitiesFragment.SINGLE)!!.entity)
        }
        findNavController().navigate(R.id.citiesFragment, bundleOf(CitiesFragment.TYPE to 0))
    }

    private fun showMap(region :Region){
        setFragmentResultListener(MapMarkerPickupsFragment.KEY_ADDRESS){ _, bundle ->
            showUserData(bundle.getParcelable(PICKUP_ADDRESS)!!)
        }
        findNavController().navigate(R.id.mapMarkerPickupsFragment,
            bundleOf(MapMarkerPickupsFragment.REGION to region.name,
            MapMarkerPickupsFragment.IS_FOR_SELLER to args.isSeller))
    }

    private fun showUserData(cdekAddress :CdekAddress){
        findNavController().navigate(R.id.myAddressPickupFragment, bundleOf(MyAddressPickupFragment.ADDRESS to cdekAddress))
    }

    companion object{
        const val ADDRESS_REQUEST = "address_request"
        const val ADDRESS_KEY = "address_key"
        const val ADDRESSES_ALL = "ALL"
        const val ADDRESSES_HOME = "home"
        const val ADDRESSES_CDEK = "cdek"
    }
}