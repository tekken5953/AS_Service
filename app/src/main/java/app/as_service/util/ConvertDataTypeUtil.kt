package app.as_service.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class ConvertDataTypeUtil {
    fun millsToString(mills: Long): String? {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("HH:mm")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = mills
        return format.format(calendar.time)
    }
}