package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.morestore.R
import com.project.morestore.adapters.PickupAddressesAdapter
import com.project.morestore.databinding.ToolbarTitledBinding
import com.project.morestore.fragments.base.MapMarkerFragment
import com.project.morestore.util.attachNavigation
import com.project.morestore.util.dp

class MapMarkerPickupsFragment :MapMarkerFragment() {
    private lateinit var behavior :BottomSheetBehavior<View>
    private lateinit var list :RecyclerView
    private val listAdapter = PickupAddressesAdapter { views.save.isEnabled = true }
    override val markerCallback: (Double, Double) -> Unit by lazy { { lat, lon ->
        listAdapter.setItems(Stubs.pickupAddresses) }
    }
    override val buttonText: String by lazy { requireContext().getString(R.string.myAddress_new_pickOnMap_button) }
    private lateinit var toolbar :ToolbarTitledBinding

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
            behavior = (views.bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior<View>
            behavior.apply {
                peekHeight = 33.dp
                addBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback() {//todo create SimpleImplementation
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        (views.buttons.getChildAt(1) as FloatingActionButton)
                            .also{
                                if(newState == STATE_EXPANDED) it.setImageResource(R.drawable.ic_cross)
                                else if (newState == STATE_COLLAPSED) it.setImageResource(R.drawable.ic_burger_menu)
                            }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        /*skip*/
                    }
                })
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(toolbar){
            title.text = "Москва, Московская область"
            root.attachNavigation()
        }
        views.save.isEnabled = false
        views.buttons.getChildAt(1).setOnClickListener {
            if(behavior.state == STATE_COLLAPSED){
                behavior.state = STATE_EXPANDED
            } else if(behavior.state == STATE_EXPANDED){
                behavior.state = STATE_COLLAPSED
            }
        }
    }

    //todo remove stubs
    private object Stubs{
        val pickupAddresses = listOf(
            "Россия, Москва, пр-т Нахимовский, 67 корп. 1",
            "Россия, Москва, ул. 11-я Парковая, 24",
            "Россия, Москва, Новочеркасский Бульвар, 20, корп. 5",
            "Россия, Москва, ул. Самаринская, 1"
        )
    }
}