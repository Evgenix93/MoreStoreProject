package com.project.morestore.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.project.morestore.R
import com.project.morestore.adapters.CitiesAdapter
import com.project.morestore.adapters.MultiplyCityAdapter
import com.project.morestore.adapters.SingleCityAdapter
import com.project.morestore.databinding.FragmentCitiesBinding
import com.project.morestore.models.Region
import com.project.morestore.mvpviews.CitiesView
import com.project.morestore.presenters.CitiesPresenter
import com.project.morestore.singletones.Network
import com.project.morestore.util.addTextChangeListener
import com.project.morestore.util.args
import com.project.morestore.util.attachNavigation
import kotlinx.parcelize.Parcelize
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.ArrayList

/* call to single select
setFragmentResultListener(REQUEST_KEY) { _, bundle ->
    val city :Region = bundle.getParcelable<ParcelRegion>(SINGLE)!!.entity
}
findNavController().navigate(R.id.citiesFragment, bundleOf(TYPE to 0)) //<-- 0 to single select
*/

/* call to multiply select
setFragmentResultListener(REQUEST_KEY) { _, bundle ->
    val cities :List<Region> = bundle.getParcelableArrayList<ParcelRegion>(MULTIPLY)!!.map { it.entity }
}
findNavController().navigate(R.id.citiesFragment,
                        bundleOf(TYPE to 1,             //<-- 1 to multiply select
                        SELECTED to longArrayOf())      //<-- optional: ids of selected cities
                    )
 */

//todo connect safeArgs
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
    private val presenter by moxyPresenter {
        val type = if(args.getInt(TYPE) == 0) CitiesPresenter.Type.SINGLE
        else CitiesPresenter.Type.MULTIPLY
        CitiesPresenter(type, args.getLongArray(SELECTED) ?: longArrayOf(), Network.cities)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCitiesBinding.inflate(inflater).also{ views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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