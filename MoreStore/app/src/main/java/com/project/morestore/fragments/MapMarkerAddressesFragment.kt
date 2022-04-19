package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.morestore.R
import com.project.morestore.adapters.SuggestsAddressesAdapter
import com.project.morestore.databinding.ToolbarTitledCenterBinding
import com.project.morestore.fragments.base.MapMarkerFragment
import com.project.morestore.models.SuggestAddress
import com.project.morestore.util.attachNavigation
import com.project.morestore.util.dp

class MapMarkerAddressesFragment :MapMarkerFragment() {
    private val listAdapter = SuggestsAddressesAdapter()
    override val markerCallback: (Double, Double) -> Unit by lazy {
        { lat, lon -> listAdapter.setItems(Stubs.addresses) }
    }
    override val buttonText: String by lazy { requireContext().getString(R.string.save) }
    private lateinit var toolbar :ToolbarTitledCenterBinding
    private lateinit var inputField :EditText
    private lateinit var list :RecyclerView

    override fun onCreateView(
        inflater :LayoutInflater,
        container :ViewGroup?,
        savedInstanceState: Bundle?
    ) = super.onCreateView(inflater, container, savedInstanceState)
        .also {
            toolbar = ToolbarTitledCenterBinding.inflate(inflater)
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
        views.buttons.removeViewAt(1)
//        layoutInflater.inflate(R.layout.widget_fab_map, views.map)
    }

    //todo remove stubs
    private object Stubs{
        val addresses = listOf(
            SuggestAddress("улица Антона Петрова", "3 км", "Барнаул, Алтайский край, Россия"),
            SuggestAddress("улица Антона Петрова, 256", "3.1 км", "Барнаул, Алтайский край, Россия"),
            SuggestAddress("улица Антона Петрова, 221Г", "3.2 км", "Барнаул, Алтайский край, Россия"),
            SuggestAddress("улица Антона Петрова, 250", "3.2 км", "Барнаул, Алтайский край, Россия"),
            SuggestAddress("улица Антона Петрова, 264", "3.2 км", "Барнаул, Алтайский край, Россия"),
            SuggestAddress("улица Антона Петрова, 187", "3.2 км", "Барнаул, Алтайский край, Россия")
        )
    }
}