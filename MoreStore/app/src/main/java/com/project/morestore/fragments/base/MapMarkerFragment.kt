package com.project.morestore.fragments.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.project.morestore.databinding.FragmentMapMarkerBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment.LEFT
import com.yandex.mapkit.logo.VerticalAlignment.TOP
import moxy.MvpAppCompatFragment

abstract class MapMarkerFragment :MvpAppCompatFragment() {
    protected abstract val buttonText :String
    protected lateinit var views :FragmentMapMarkerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
        lifecycle.addObserver(object :LifecycleEventObserver{
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when(event){//other ignored
                    Lifecycle.Event.ON_START -> {
                        views.map.onStart()
                        MapKitFactory.getInstance().onStart()
                    }
                    Lifecycle.Event.ON_STOP -> {
                        views.map.onStop()
                        MapKitFactory.getInstance().onStop()
                    }
                    else -> {}//ignore
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMapMarkerBinding.inflate(inflater).also { views = it }.root

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.save.text = buttonText
        views.map.map.isRotateGesturesEnabled = false
        views.map.map.logo.setAlignment(Alignment(LEFT, TOP))
    }
}