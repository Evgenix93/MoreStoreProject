package com.project.morestore.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.project.morestore.R
import com.project.morestore.presentation.adapters.MediaAdapter
import com.project.morestore.databinding.FragmentMediaBinding
import com.project.morestore.presentation.fragments.base.FullscreenFragment

import com.project.morestore.domain.presenters.MediaPresenter
import com.project.morestore.presentation.mvpviews.BaseMvpView
import com.project.morestore.util.createRect
import com.project.morestore.util.dp
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class MediaFragment() : FullscreenFragment(), BaseMvpView {
    private lateinit var views :FragmentMediaBinding
    @Inject
    lateinit var mainPresenter: MediaPresenter
    private val presenter by moxyPresenter { mainPresenter }
    private var currentPage :Int = 0

    companion object{
        const val PHOTOS = "photos"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMediaBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBarsColor(Color.BLACK)
        val photos = arguments?.getStringArray(PHOTOS)?: arrayOf()
        with(views){
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
            textIndicator.text = "1 из ${photos.size}"
            indicator.dividerDrawable = createRect(4.dp, 0)
            for(i in 1..photos.size){
                indicator.addView(createIndicator()
                    .apply { changeBgColor(this, R.color.gray1) })
            }
            indicator.getChildAt(0).apply { changeBgColor(this, R.color.white) }
            pager.adapter = MediaAdapter(this@MediaFragment, photos){uri ->
                presenter.playVideo(fileUri = uri.toUri())

            }
            pager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    changeBgColor(indicator.getChildAt(currentPage), R.color.gray1)
                    currentPage = position
                    textIndicator.text = "${currentPage + 1} из ${photos.size}"
                    changeBgColor(indicator.getChildAt(currentPage), R.color.white)
                }
            })
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        setBarsColor(Color.WHITE)
    }

    private fun createIndicator() :View{
        return View(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(6.dp, 6.dp)
            setBackgroundResource(R.drawable.bg_rectangle_rounded2)
        }
    }

    private fun changeBgColor(view :View, @ColorRes colorId :Int){
        DrawableCompat.setTint(view.background, ContextCompat.getColor(requireContext(), colorId))
    }

    private fun setBarsColor(color :Int){
        if(color == Color.BLACK){
            clearLightStatusBar(requireActivity())
        } else {
            setLightStatusBar(requireActivity())
        }
    }

    private fun setLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            activity.window.decorView.systemUiVisibility = flags
            activity.window.statusBarColor = Color.WHITE
            activity.window.navigationBarColor = Color.WHITE
        }
    }

    private fun clearLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            activity.window.decorView.systemUiVisibility = flags
            activity.window.statusBarColor = Color.BLACK
            activity.window.navigationBarColor = Color.BLACK
        }
    }

    override fun loaded(result: Any) {
        views.loader.isVisible = false
        startActivity(result as Intent)

    }

    override fun loading() {
        views.loader.isVisible = true

    }

    override fun error(message: String) {
        views.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    }


}