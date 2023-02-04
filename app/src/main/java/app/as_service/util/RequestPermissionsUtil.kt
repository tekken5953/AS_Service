package app.as_service.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import app.as_service.dao.StaticDataObject.REQUEST_LOCATION

class RequestPermissionsUtil {

    // 위치정보 권한 요청
    fun requestLocation(activity: Activity) {
        if(ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf (
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_LOCATION)
        }
    }
}