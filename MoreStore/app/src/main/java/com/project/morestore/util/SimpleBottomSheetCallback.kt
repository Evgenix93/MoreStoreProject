package com.project.morestore.util

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * Implementations with empty methods, for Interface Segregation Principle
 */


abstract class SimpleBottomSheetCallback :BottomSheetBehavior.BottomSheetCallback(){
    override fun onStateChanged(bottomSheet: View, newState: Int) { /* reserved */ }

    override fun onSlide(bottomSheet: View, slideOffset: Float) { /* reserved */ }
}