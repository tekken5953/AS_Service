package app.as_service.api

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import app.as_service.dao.StaticDataObject
import app.as_service.util.ConvertDataTypeUtil
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
class GetLocation() {
    private var x = "0"
    private var y = "0"
    private var s = "null"

    fun getInstance(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val geocoder = Geocoder(context)
                    geocoder.getFromLocation(it.latitude, it.longitude, 1)?.get(0)?.let { address ->
                        // 위도 경도를 x y 좌표로 변환
                        val convertGrid = ConvertDataTypeUtil.convertGridGps(
                            0,
                            address.latitude,
                            address.longitude
                        )
                        Log.d(
                            StaticDataObject.TAG_G,
                            "latitude : ${address.latitude} longitude : ${address.longitude}"
                        )

                        x = convertGrid.x.toInt().toString()
                        y = convertGrid.y.toInt().toString()
                        s = "${address.adminArea} ${address.locality} ${address.thoroughfare}"
                        Log.d(StaticDataObject.TAG_G, "${x}_${y}_${s}")
                    }
                }
            }
    }

    fun execute() : String {
        return "${x}_${y}_${s}"
    }
}