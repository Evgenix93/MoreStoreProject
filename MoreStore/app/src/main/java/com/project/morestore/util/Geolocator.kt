package com.project.morestore

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Geolocator @Inject constructor(@ApplicationContext val context :Context){

    suspend fun getCurrentPosition() :GeoPosition? = suspendCoroutine { coroutine ->
        getCurrentPosition { position -> coroutine.resume(position) }
    }

    fun getCurrentPosition(callback :(GeoPosition?)->Unit){
        try {
            LocationServices.getFusedLocationProviderClient(context)
                .getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, SimpleLocationCancel())
                .addOnCompleteListener { task ->
                    Log.d("Geolocator", "task complete")
                    if(task.isSuccessful) callback(task.result?.let { GeoPosition(it) })
                    else callback(null)
                }
        }catch (e: SecurityException){
            //todo implement
        }
    }

    private class SimpleLocationCancel : CancellationToken(){
        override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken = this
        override fun isCancellationRequested(): Boolean = false
    }
}

data class GeoPosition(val lat :Double, val lon :Double){
    constructor(location :Location) :this(location.latitude, location.longitude)
}