package app.as_service.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.as_service.dao.StaticDataObject.REQUEST_LOCATION
import app.as_service.dao.StaticDataObject.REQUEST_NOTIFICATION

class RequestPermissionsUtil(activity: Activity) {
    val context = activity
    private val permissionsLocation = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
//    private val permissionNotification = arrayOf(if (Build.VERSION.SDK_INT >= 33) {
////        Manifest.permission.POST_NOTIFICATIONS
//    } else { null })

    // 위치정보 권한 요청
    fun requestLocation() {
        if(ActivityCompat.checkSelfPermission(context,
                permissionsLocation[0]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context,
                permissionsLocation[1]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, permissionsLocation, REQUEST_LOCATION)
        }
    }

//    fun requestNotification() {
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                permissionNotification.toString()
//            ) != PackageManager.PERMISSION_GRANTED)
//            {
//            ActivityCompat.requestPermissions(context, permissionNotification, REQUEST_NOTIFICATION)
//        }
//    }

    //권한을 허락 받아야함
    fun isPermitted(): Boolean {
        for (perm in permissionsLocation) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}