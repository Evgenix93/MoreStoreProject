package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.MaterialAdapter
import com.project.morestore.databinding.FragmentCreateProductMaterialsBinding
import com.project.morestore.dialogs.SaveProductDialog
import com.project.morestore.data.models.*
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductMaterialsFragment: MvpAppCompatFragment(R.layout.fragment_create_product_materials), MainMvpView {
    private val binding: FragmentCreateProductMaterialsBinding by viewBinding()
    private var materialAdapter: MaterialAdapter by autoCleared()
    @Inject lateinit var mainPresenter: MainPresenter
    private val presenter by moxyPresenter { mainPresenter }
    private var searchInitiated = false
    private var materialProperties: List<Property2>? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        loadMaterials()
        initToolbar()
        loadChosenMaterials()
    }



    private fun initList(){
        materialAdapter = MaterialAdapter(requireContext(),false){initSaveButton(it)}
        with(binding.materialsRecyclerView){
            adapter = materialAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

    }

    private fun initSaveButton(isChecked: Boolean){

        if(isChecked){
            binding.saveButton.isEnabled = true
            binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
        }else{
            binding.saveButton.isEnabled = false
            binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }

        binding.saveButton.setOnClickListener{
            saveMaterials()
            findNavController().popBackStack()
        }
    }

    private fun saveMaterials(){
        val properties = materialAdapter.getCurrentMaterials().filter{it.isSelected}.map{Property2(it.id, it.idCategory.toLong())}
        Log.d("Debug", "material properties = $properties")
        presenter.removeProperty(13)
        presenter.updateCreateProductData(extProperties = properties)
    }

    private fun initSearch(materials: List<Property>){
        val searchEditText = binding.searchEditText
        val searchFlow = kotlinx.coroutines.flow.callbackFlow<kotlin.String> {
            val searchListener =
                object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {

                    }

                    override fun onTextChanged(
                        newText: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {
                        trySendBlocking(newText.toString())

                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }

                }
            searchEditText.addTextChangedListener(searchListener)
            awaitClose { searchEditText.removeTextChangedListener(searchListener) }
        }
        presenter.collectMaterialSearchFlow(searchFlow, materials)

    }

    private fun loadMaterials(){
        presenter.getMaterials()
    }

    private fun loadChosenMaterials(){
        presenter.loadCreateProductData()
    }


    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun success() {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        when (result){
            is CreatedProductId -> findNavController().navigate(R.id.mainFragment)

            is List<*> -> {

                materialAdapter.updateList((result as List<Property>).map { MaterialLine(it.id, it.name, false, idCategory = it.idCategory?.toInt()!!) } )
                val properties = result
                materialProperties?.let{
                    it.forEach {materialProperty ->
                        properties.find{property ->
                            property.id == materialProperty.value
                        }?.isChecked = true
                    }
                }
                materialAdapter.updateList((properties).map { MaterialLine(it.id, it.name, it.isChecked == true, idCategory = it.idCategory?.toInt()!!) } )

                if(!searchInitiated){
                    initSearch(result as List<Property>)
                    searchInitiated = true
                }
                initSaveButton(properties.any{it.isChecked == true})
            }
            is CreateProductData -> {
                materialProperties = result.property?.filter{ property ->
                        13L == property.propertyCategory
                }
                loadMaterials()
                binding.toolbar.actionIcon.setOnClickListener {
                    if(result.id == null)
                        SaveProductDialog { presenter.createDraftProduct() }.show(childFragmentManager, null)
                    else
                        findNavController().popBackStack(findNavController().previousBackStackEntry!!.destination.id, true)
                }
            }
        }
    }

}