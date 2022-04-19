package com.project.morestore.presenters

import com.project.morestore.apis.CitiesApi
import com.project.morestore.models.Region
import com.project.morestore.mvpviews.CitiesView
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class CitiesPresenter(
    private val type :Type,
    private val selectedIds :LongArray,
    private val network :CitiesApi
) : MvpPresenter<CitiesView>() {
    private var cities = arrayOf<Region>()
    private var filtered = listOf<Region>()
        set(value){
            field = value
            viewState.showCities(field.toTypedArray())
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.loading(true)
        presenterScope.launch {
            val networkCities = network.getCities()
            viewState.loading(false)
            if(type == Type.SINGLE) {
                cities = networkCities
            } else {
                cities = arrayOf(Region(0, "Все города", 0, false), *networkCities)
                if(selectedIds.isNotEmpty() && selectedIds[0] == 0L) cities.forEach { it.isChecked = true }
                else cities.forEach { it.isChecked = selectedIds.contains(it.id) }
            }
            filtered = cities.toList()
        }
    }

    fun search(request :String){
        presenterScope.launch {
            filtered = cities.filter { it.name.contains(request, true) }
        }
    }

    enum class Type { SINGLE, MULTIPLY }
}