package com.project.morestore.fragments

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.project.morestore.Geolocator
import com.project.morestore.R
import com.project.morestore.adapters.SuggestsAddressesAdapter
import com.project.morestore.databinding.ToolbarTitledCenterBinding
import com.project.morestore.fragments.base.MapMarkerFragment
import com.project.morestore.models.SuggestAddress
import com.project.morestore.mvpviews.MapMarkerAddressesView
import com.project.morestore.presenters.MapMarkerAddressesPresenter
import com.project.morestore.util.attachNavigation
import com.project.morestore.util.dp
import com.project.morestore.util.string
import com.yandex.mapkit.Animation
import com.yandex.mapkit.Animation.Type.SMOOTH
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.runtime.ui_view.ViewProvider
import moxy.ktx.moxyPresenter

class MapMarkerAddressesFragment :MapMarkerFragment(), MapMarkerAddressesView {

    companion object{
        const val KEY = "find_on_map"
        const val CITY = "city"
        const val STREET = "street"
        const val INDEX = "index"
        const val HOUSE = "house"
    }

    private val listAdapter = SuggestsAddressesAdapter {views.save.isEnabled = it}
    override val buttonText: String by lazy { requireContext().getString(R.string.save) }
    private lateinit var toolbar :ToolbarTitledCenterBinding
    private lateinit var inputField :EditText
    private lateinit var list :RecyclerView
    private val bottomBehavior : BottomSheetBehavior<LinearLayout> by lazy {
        BottomSheetBehavior.from(views.bottomSheet)
    }
    private val presenter :MapMarkerAddressesPresenter by moxyPresenter {
        SearchFactory.initialize(requireContext());
        val search = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        MapMarkerAddressesPresenter(search, Geolocator(requireContext()))
    }
    private lateinit var searchMarker :PlacemarkMapObject
    private val markerListener = object :SimpleMapItemDragListener{
        override fun onMapObjectDragEnd(p0: MapObject) {
            super.onMapObjectDragEnd(p0)
            val position = (p0 as PlacemarkMapObject).geometry
            presenter.onMarkerMove(position.latitude, position.longitude)
        }
    }

    override fun onCreateView(
        inflater :LayoutInflater,
        container :ViewGroup?,
        savedInstanceState: Bundle?
    ) = super.onCreateView(inflater, container, savedInstanceState)
        .also {
            toolbar = ToolbarTitledCenterBinding.inflate(inflater)//fixme move toolbar to layout
            it.addView(toolbar.root, 0)
            it.findViewById<LinearLayout>(R.id.bottomSheet)
                .apply {
                    inflater.inflate(R.layout.layout_suggestedaddresses, this)
                        .also {
                            inputField = it.findViewById(R.id.addressInput)
                            list = it.findViewById(R.id.list)
                            list.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
                            list.adapter = listAdapter
                        }
                }
            ((views.bottomSheet.layoutParams as CoordinatorLayout.LayoutParams)
                .behavior as BottomSheetBehavior).peekHeight = 53.dp
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(toolbar){
            title.setText(R.string.myAddress_new_title)
            root.attachNavigation()
        }
        searchMarker = views.map.map.mapObjects.addPlacemark(
            Point(55.754892, 37.618751),
            ViewProvider(View(requireContext()).apply{
                setBackgroundResource(R.drawable.ic_geomarker_select)
            }),//todo change to bitmap
            IconStyle().setAnchor(PointF(0.5f, 1f))
        ).apply{
            isDraggable = true
            setDragListener(markerListener)
        }
        inputField.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_SEARCH) {
                presenter.onSearch(
                    inputField.string,
                    VisibleRegionUtils.toPolygon(views.map.map.visibleRegion),
                )
            }
            false
        }
        //todo replace deleting by adding button
        views.buttons.removeViewAt(1) //remove list button from parent view
        views.currentPositionButton.setOnClickListener{
            presenter.moveToCurrentPosition()
        }
        views.save.setOnClickListener {
            listAdapter.selected?.let {
                setFragmentResult(KEY, bundleOf(
                    CITY to it.city,
                    STREET to it.street,
                    INDEX to it.index,
                    HOUSE to it.house
                ))
                findNavController().popBackStack()
            }
        }
    }

    //region View implementation
    override fun showAddresses(addresses: Array<SuggestAddress>) {
        Log.d("maplog", "showAddresses")
        if(addresses.isEmpty())
            showMessage("Адрес не найден")
        listAdapter.setItems(addresses)
        if(addresses.size == 1) inputField.setText(addresses.first().address)
    }

    override fun showList() {
        bottomBehavior.state = STATE_EXPANDED
    }

    override fun navigateMap(point :Point) {
        CameraPosition(point, 16f, 0f, 0f)
            .also { views.map.map.move(it, Animation(SMOOTH, 0f), null) }
        searchMarker.geometry = point
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    //endregion View implementation

    private interface SimpleMapItemDragListener :MapObjectDragListener{
        override fun onMapObjectDragStart(p0: MapObject) { /* reserved */ }

        override fun onMapObjectDrag(p0: MapObject, p1: Point) { /* reserved */ }

        override fun onMapObjectDragEnd(p0: MapObject) { /* reserved */ }
    }
}