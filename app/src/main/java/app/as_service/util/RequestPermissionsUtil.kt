package app.as_service.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.as_service.dao.StaticDataObject.REQUEST_LOCATION

class RequestPermissionsUtil(activity: Activity) {
    val context = activity
    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    // 위치정보 권한 요청
    fun requestLocation() {
        if(ActivityCompat.checkSelfPermission(context,
                permissions[0]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context,
                permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, permissions, REQUEST_LOCATION)
        }
    }

    //권한을 허락 받아야함
    fun isPermitted(): Boolean {
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}