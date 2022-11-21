package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.presentation.adapters.CitiesAdapter
import com.project.morestore.presentation.adapters.MultiplyCityAdapter
import com.project.morestore.presentation.adapters.SingleCityAdapter
import com.project.morestore.databinding.FragmentCitiesBinding
import com.project.morestore.data.models.Region
import com.project.morestore.presentation.mvpviews.CitiesView
import com.project.morestore.domain.presenters.CitiesPresenter
import com.project.morestore.data.singletones.Token
import com.project.morestore.util.addTextChangeListener
import com.project.morestore.util.args
import com.project.morestore.util.attachNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class CitiesFragment :MvpAppCompatFragment(), CitiesView {
    companion object {
        const val REQUEST_KEY = "city"
        const val TYPE = "type"
        const val SINGLE = "single"
        const val MULTIPLY = "multiply"
        const val SELECTED = "selected"
    }
    private lateinit var views :FragmentCitiesBinding
    private lateinit var adapter :CitiesAdapter
    @Inject lateinit var citiesPresenter: CitiesPresenter
    private val presenter by moxyPresenter {
        citiesPresenter
    }
    private var isCitiesFirstLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCitiesBinding.inflate(inflater).also{ views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = if(args.getInt(TYPE) == 0) CitiesPresenter.Type.SINGLE
        else CitiesPresenter.Type.MULTIPLY
        presenter.getCities(type, args.getLongArray(SELECTED) ?: longArrayOf())
        if(args.getInt(TYPE) == 0){
            adapter = SingleCityAdapter{
                setFragmentResult(REQUEST_KEY, bundleOf(SINGLE to ParcelRegion(it)))
                findNavController().navigateUp()
            }
            views.apply.visibility = GONE
        } else {
            adapter = MultiplyCityAdapter()
            views.apply.setOnClickListener {
                val multiplyAdapter = adapter as MultiplyCityAdapter
                val selected = multiplyAdapter.selected.map { ParcelRegion(it) }
                val rCities = if(selected.isNotEmpty() && selected[0].id == 0L) {
                    ArrayList<ParcelRegion>().apply { add(selected[0]) }
                } else {
                    selected
                }
                setFragmentResult(REQUEST_KEY, bundleOf(MULTIPLY to rCities))
                findNavController().navigateUp()
            }
        }
        with(views.toolbar){
            root.attachNavigation()
            title.setText(R.string.searchCity)
        }
        with(views){
            list.adapter = adapter
            editText3.addTextChangeListener { presenter.search(it) }
            if(Token.currentUserAddress != null){
                imageView25.isVisible = true
                textView42.isVisible = true
                textView42.text = Token.currentUserAddress?.fullAddress?.substringBefore(",")

            }
        }
    }

    //region View implementation
    override fun loading(show: Boolean) {
        views.loader.visibility = if(show) VISIBLE else GONE
    }

    override fun showMessage(message :String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showCities(cities: Array<Region>) {
        adapter.items = cities
        if(!isCitiesFirstLoaded){
            Log.d("mylog", cities.toString())
            val region = cities.find { it.name == Token.currentUserAddress?.fullAddress?.substringBefore(",") }
            if(region != null){
                views.textView42.setOnClickListener {
                    setFragmentResult(REQUEST_KEY, bundleOf(SINGLE to ParcelRegion(region)))
                    findNavController().navigateUp()
                }
            }
            isCitiesFirstLoaded = true
        }

    }
    //endregion View implementation
}

@Parcelize
data class ParcelRegion(
    val id: Long,
    val name: String,
    val idCountry: Long,
    var isChecked: Boolean
    ) :Parcelable {
        constructor(region :Region)
                :this(region.id, region.name, region.idCountry, region.isChecked)

        val entity get() = Region(id, name, idCountry, isChecked)
    }
