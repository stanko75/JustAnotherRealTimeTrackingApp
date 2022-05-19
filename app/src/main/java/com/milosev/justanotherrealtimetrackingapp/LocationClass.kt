package com.milosev.justanotherrealtimetrackingapp

import android.content.Context
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class LocationClass
    (context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback
    private var locationRequest: LocationRequest

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 50000
            smallestDisplacement = 2f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location =
                        locationResult.lastLocation

                    val locationModel = LocationModel()
                    locationModel.lat = location.latitude.toString()
                    locationModel.lng = location.longitude.toString()
                    //locationModelList.add(locationModel)
                    val sdf = SimpleDateFormat("ddMyyyyhhmmss", Locale.GERMANY)
                    val currentDate = sdf.format(Date())
                    val builder = GsonBuilder()
                    val gson: Gson = builder.create()
                    System.out.println(gson.toJson(locationModel))
                    writeFileOnInternalStorage(
                        context,
                        "test$currentDate",
                        gson.toJson(locationModel)
                    )

                }
            }
        }
    }

    fun writeFileOnInternalStorage(mcoContext: Context, sFileName: String?, sBody: String?) {
        val path = mcoContext.getExternalFilesDir(null)
        val dir = File(path, "locations")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val gpxfile = sFileName?.let { File(dir, it) }
            val writer = FileWriter(gpxfile)
            writer.append(sBody)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}