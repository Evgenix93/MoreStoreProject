package com.project.morestore.fragments

import android.Manifest
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.project.morestore.Geolocator
import com.project.morestore.R
import com.project.morestore.adapters.PickupAddressesAdapter
import com.project.morestore.databinding.ToolbarTitledBinding
import com.project.morestore.fragments.base.MapMarkerFragment
import com.project.morestore.models.CdekAddress
import com.project.morestore.models.DeliveryAddress
import com.project.morestore.mvpviews.MapMarkerPickupsView
import com.project.morestore.mvpviews.MapMarkerPickupsView.NavigateType.*
import com.project.morestore.presenters.MapMarkerPickupsPresenter
import com.project.morestore.singletones.Network
import com.project.morestore.util.SimpleBottomSheetCallback
import com.project.morestore.util.args
import com.project.morestore.util.attachNavigation
import com.project.morestore.util.dp
import com.project.morestore.widgets.loading.LoadingDialog
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.runtime.ui_view.ViewProvider
import moxy.ktx.moxyPresenter

class MapMarkerPickupsFragment :MapMarkerFragment(), MapMarkerPickupsView, CameraListener {
    companion object{
        const val REGION = "region"
        const val KEY_ADDRESS = "key_address"
        const val PICKUP_ADDRESS = "pickup_address"
    }
    private val behavior :BottomSheetBehavior<View> by lazy {
        (views.bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior<View>
    }
    private lateinit var list :RecyclerView
    private val listAdapter = PickupAddressesAdapter {
        presenter.selectAddress(it)
    }
    override val buttonText: String by lazy { requireContext().getString(R.string.myAddress_new_pickOnMap_button) }
    private lateinit var toolbar :ToolbarTitledBinding
    private val searchRegion get() = args.getString(REGION)!!
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
            if(granted) presenter.onGeopositionClick()
        }
    private lateinit var userMarker : PlacemarkMapObject
    private var loading :LoadingDialog? = null
    private val presenter :MapMarkerPickupsPresenter by moxyPresenter {
        SearchFactory.initialize(requireContext());
        val search = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        MapMarkerPickupsPresenter(search, Geolocator(requireContext()), searchRegion, Network.cdekAddresses)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState: Bundle?
    ) = super.onCreateView(inflater, container, savedInstanceState)
        .also {
            toolbar = ToolbarTitledBinding.inflate(inflater)
            it.addView(toolbar.root, 0)

            it.findViewById<LinearLayout>(R.id.bottomSheet)
                .apply{
                    inflater.inflate(R.layout.layout_pickupaddresses, this)
                        .also{
                            list = it.findViewById(R.id.list)
                            list.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
                            list.adapter = listAdapter
                        }
                }
            behavior.apply {
                peekHeight = 33.dp
                addBottomSheetCallback(object :SimpleBottomSheetCallback() {
                    private val button by lazy { views.buttons.getChildAt(1) as ImageView }
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if(newState == STATE_EXPANDED) button.setImageResource(R.drawable.ic_cross)
                        else if (newState == STATE_COLLAPSED) button.setImageResource(R.drawable.ic_burger_menu)
                    }
                })
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(toolbar){
            title.text = searchRegion
            root.attachNavigation()
        }
        views.save.isEnabled = false
        views.save.setOnClickListener { presenter.save() }
        views.buttons.getChildAt(0).setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        views.buttons.getChildAt(1).setOnClickListener {//todo handle via presenter
            if(behavior.state == STATE_COLLAPSED){
                behavior.state = STATE_EXPANDED
            } else if(behavior.state == STATE_EXPANDED){
                behavior.state = STATE_COLLAPSED
            }
        }
        views.map.map.addCameraListener(this)
    }

    //region View implementation
    override fun moveMap(point: Point, type :MapMarkerPickupsView.NavigateType) {
        val zoom = when(type){
            CITY -> 12f
            USER -> 16f
            NONE -> views.map.map.cameraPosition.zoom
        }
        CameraPosition(point, zoom, 0f, 0f)
            .also { views.map.map.move(it, Animation(Animation.Type.LINEAR, 0f), null) }
        if(type != USER) return
        userMarker = views.map.map.mapObjects.addPlacemark(
            point,
            ViewProvider(View(requireContext()).apply{
                setBackgroundResource(R.drawable.ic_geomarker_select)
            }),//todo change to bitmap
            IconStyle().setAnchor(PointF(0.5f, 1f))
        )
    }

    override fun showAddresses(addresses: Array<CdekAddress>) {
        listAdapter.setItems(addresses.map { it })

        addresses.forEach {
            views.map.map.mapObjects.addPlacemark(
                Point(it.location.lat, it.location.lon),
                ViewProvider(View(requireContext()).apply{
                    setBackgroundResource(R.drawable.ic_geomarker_pickup)
                }),//todo change to bitmap
                IconStyle().setAnchor(PointF(0.5f, 1f))
            ).apply {
                addTapListener { _, _ -> presenter.selectMarker(it); true }
            }
        }
        //todo filter marker on new/showed/outside
        //todo add only new markers
        //todo remove outside markers
    }

    override fun showLoading(show: Boolean) {
        if(show){
            this.loading = LoadingDialog(requireContext())
            this.loading!!.show()
        } else {
            this.loading?.dismiss()
            this.loading = null
        }
    }

    override fun indicateSelected(selectAddress: CdekAddress) {
        listAdapter.select(selectAddress)
    }

    override fun enableNext(enable: Boolean) {
        views.save.isEnabled = enable
    }

    override fun returnAddress(address: CdekAddress) {
        setFragmentResult(KEY_ADDRESS, bundleOf(PICKUP_ADDRESS to address))
        findNavController().navigateUp()
    }

    override fun showList() {
        behavior.state = STATE_EXPANDED
    }

    //endregion View implementation

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        p3: Boolean
    ) {
        if(p3)presenter.onMoveMap(p0.visibleRegion)
    }
}