package com.project.morestore.presenters

import com.project.morestore.apis.CitiesApi
import com.project.morestore.data.models.Region
import com.project.morestore.mvpviews.CitiesView
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class CitiesPresenter @Inject constructor(
    private val network :CitiesApi
) : MvpPresenter<CitiesView>() {
    private var cities = listOf<Region>()
    private var filtered = listOf<Region>()
        set(value){
            field = value
            viewState.showCities(field.toTypedArray())
        }

     fun getCities(type: Type, selectedIds: LongArray) {
         viewState.loading(true)
        presenterScope.launch {
            val networkCities = network.getCities()
            viewState.loading(false)
            if(type == Type.SINGLE) {
                cities = networkCities
            } else {
                cities = listOf(Region(0, "Все города", 0, false), *networkCities.toTypedArray())
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