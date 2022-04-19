package com.project.morestore.fragments.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.project.morestore.databinding.FragmentMapMarkerBinding

abstract class MapMarkerFragment :Fragment() {//todo create child fragment
    protected abstract val markerCallback :(Double, Double) -> Unit
    protected abstract val buttonText :String
    protected lateinit var views :FragmentMapMarkerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMapMarkerBinding.inflate(inflater).also { views = it }.root

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.save.text = buttonText
        //todo add marker programmatically
        views.marker.setOnTouchListener { v, event ->
            val topMargin = views.root.getChildAt(0).height
            val params = v.layoutParams as CoordinatorLayout.LayoutParams
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    params.topMargin = event.rawY.toInt() - (v.height + topMargin)
                    params.leftMargin = event.rawX.toInt() - (v.width / 2)
                    v.layoutParams = params
                    markerCallback.invoke(0.0, 0.0)//todo implement
                }
                MotionEvent.ACTION_MOVE -> {
                    params.topMargin = event.rawY.toInt() - (v.height + topMargin)
                    params.leftMargin = event.rawX.toInt() - (v.width / 2)
                    v.layoutParams = params
                }
                MotionEvent.ACTION_DOWN -> {
                    params.gravity = Gravity.START or Gravity.TOP
                    v.layoutParams = params
                }
            }
            true
        }
    }
}