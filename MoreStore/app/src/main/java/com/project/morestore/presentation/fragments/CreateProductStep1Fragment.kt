package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.ForWhoCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep1Binding
import com.project.morestore.domain.presenters.CreateProductPresenter
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductStep1Fragment : MvpAppCompatFragment(R.layout.fragment_create_product_step1),
    CreateProductMvpView {
    private val binding: FragmentCreateProductStep1Binding by viewBinding()
    private var forWhoAdapter: ForWhoCreateProductAdapter by autoCleared()
    @Inject
    lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenCheck()
        initList()
        setClickListeners()
        initToolbar()
        clearCreateProductData()
    }


    private fun setClickListeners() {
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep1FragmentDirections.actionCreateProductStep1FragmentToCreateProductHowToSellFragment())
        }
    }

    private fun initToolbar() {
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 1 из 5"

        binding.toolbar.actionIcon.setOnClickListener {
            findNavController().navigate(
                CreateProductStep1FragmentDirections.actionCreateProductStep1FragmentToMainFragment()
            )
        }
    }

    private fun initList() {
        forWhoAdapter = ForWhoCreateProductAdapter { position ->
            findNavController().navigate(
                CreateProductStep1FragmentDirections.actionCreateProductStep1FragmentToCreateProductStep2Fragment(
                    position
                )
            )

        }
        with(binding.itemsList) {
            adapter = forWhoAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        forWhoAdapter.notifyDataSetChanged()

    }

    private fun tokenCheck(){
        presenter.tokenCheck()
    }

    private fun clearCreateProductData() {
        presenter.clearCreateProductData()
    }


    override fun loaded(result: Any) {
        binding.loader.isVisible = false
       val isTokenEmpty = result as Boolean
        if(isTokenEmpty) {
            val navOptions =  NavOptions.Builder().setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false).build()
            findNavController().navigate(
                R.id.cabinetGuestFragment,
                bundleOf(CabinetGuestFragment.FRAGMENT_ID to R.id.createProductStep1Fragment),
                navOptions
            )
        }
    }

    override fun loading() {
        binding.loader.isVisible = true
    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


}