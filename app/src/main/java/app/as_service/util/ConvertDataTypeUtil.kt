
package app.as_service.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*


object ConvertDataTypeUtil {
    fun millsToString(mills: Long, pattern: String): String {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat(pattern,Locale.KOREA)
        return format.format(Date(mills))
    }

    fun longToMillsString(mills: Long, pattern: String): String {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat(pattern,Locale.KOREA)
        return format.format(Date(mills * 1000))
    }

    // 현재 시간 불러오기
    fun getCurrentTimeMills() : Long {
        return System.currentTimeMillis()
    }

    // 어제시간 반환
    fun getYesterdayLong() : Long {
        val currentTime = getCurrentTimeMills()
        return currentTime - (1000 * 60 * 60 * 24)
    }

    // 국가를 대한민국으로 설정합니다
    fun setLocaleToKorea(context: Context) {
        val configuration = Configuration()
        configuration.setLocale(Locale.KOREA)
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    // 국가를 영어권으로 설정합니다
    fun setLocaleToEnglish(context: Context) {
        val configuration = Configuration()
        configuration.setLocale(Locale.ENGLISH)
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    // 날짜 형식을 변환
    fun convertBaseDateToFormatDate(baseDate: String): String {
        return "${baseDate.substring(0, 4)}년 ${baseDate.substring(4, 6)}월 ${
            baseDate.substring(
                6,
                baseDate.length
            )
        }일"
    }

    // 시간 형식을 변환
    fun convertBaseTimeToFormatTime(baseTime: String): String {
        return "${baseTime.substring(0, 2)}시 ${baseTime.substring(2, baseTime.length)}분"
    }

    // 위도, 경도 -> X축, Y축
    fun convertGridGps(mode: Int, lat_X: Double, lng_Y: Double): LatXLngY {
        // https://gist.github.com/fronteer-kr/14d7f779d52a21ac2f16 : Reference

        val TO_GRID = 0
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기1준점 Y좌표(GRID)

        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )

        val degRad = PI / 180.0
        val radDeg = 180.0 / PI
        val re = RE / GRID
        val sLat1 = SLAT1 * degRad
        val sLat2 = SLAT2 * degRad
        val oLon = OLON * degRad
        val oLat = OLAT * degRad
        var sn = tan(PI * 0.25 + sLat2 * 0.5) / tan(PI * 0.25 + sLat1 * 0.5)
        sn = ln(cos(sLat1) / cos(sLat2)) / ln(sn)
        var sf = tan(PI * 0.25 + sLat1 * 0.5)
        sf = sf.pow(sn) * cos(sLat1) / sn
        var ro = tan(PI * 0.25 + oLat * 0.5)
        ro = re * sf / ro.pow(sn)
        val rs = LatXLngY()
        if (mode == TO_GRID) {
            rs.lat = lat_X
            rs.lng = lng_Y
            var ra = tan(PI * 0.25 + lat_X * degRad * 0.5)
            ra = re * sf / ra.pow(sn)
            var theta = lng_Y * degRad - oLon
            if (theta > PI) theta -= 2.0 * PI
            if (theta < -PI) theta += 2.0 * PI
            theta *= sn
            rs.x = floor(ra * sin(theta) + XO + 0.5)
            rs.y = floor(ro - ra * cos(theta) + YO + 0.5)
        } else {
            rs.x = lat_X
            rs.y = lng_Y
            val xn = lat_X - XO
            val yn = ro - lng_Y + YO
            var ra = sqrt(xn * xn + yn * yn)
            if (sn < 0.0) {
                ra = -ra
            }
            var alat = (re * sf / ra).pow(1.0 / sn)
            alat = 2.0 * atan(alat) - PI * 0.5
            var theta: Double
            if (abs(xn) <= 0.0) {
                theta = 0.0
            } else {
                if (abs(yn) <= 0.0) {
                    theta = PI * 0.5
                    if (xn < 0.0) {
                        theta = -theta
                    }
                } else theta = atan2(xn, yn)
            }
            val alon = theta / sn + oLon
            rs.lat = alat * radDeg
            rs.lng = alon * radDeg
        }
        return rs
    }

    // 리턴 데이터 타입지정
    class LatXLngY {
        var lat = 0.0
        var lng = 0.0
        var x = 0.0
        var y = 0.0
    }
}

