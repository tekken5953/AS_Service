package app.as_service.api

import android.util.Log
import app.as_service.dao.IgnoredKeyFile.decodingKey
import app.as_service.dao.IgnoredKeyFile.weatherApiURL
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.repository.BaseRepository
import app.as_service.util.ConvertDataTypeUtil.getCurrentTimeMills
import app.as_service.util.ConvertDataTypeUtil.getYesterdayLong
import app.as_service.util.ConvertDataTypeUtil.millsToString
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder.encode

class WeatherApiExplorer : BaseRepository() {
    /**
    "PTY" -> "강수형태 - 없음(0), 비(1), 비/눈(2), 눈(3) 빗방울(5), 빗방울눈날림(6), 눈날림(7)"
    "T1H" -> "기온(℃)"
    "RN1" -> "1시간 강수량(mm)"
    "UUU" -> "동서바람성분(m/s)"
    "VVV" -> "남북바람성분(m/s)"
    "REH" -> "습도(%)"
    "WSD" -> "풍속(m/s)"
    "VEC" -> "풍향(deg)"
    "POP" -> "강수확률(%)"
    "PCP" -> "1시간 강수량(mm)"
    "SNO" -> "1시간 신적설(cm)"
    "SKY" -> "하늘상태 - 맑음(1),구름많음(3),흐림(4)\n"
    "TMP" -> "1시간 기온(℃)"
    "TMN" -> "일 최저기온(℃)"
    "TMX" -> "일 최고기온(℃)"
    "WAV" -> "파고(M)"
     **/

    /**
    "getUltraSrtNcst" // 초단기실황조회
    numOfUltraSrtNcst = "8"
    "getUltraSrtFcst" // 초단기예보조회
    numOfUltraSrtFcst = "10"
    "getVilageFcst" // 단기예보조회
    numOfRowsVilageFcst = "14"
    "getFcstVersion" // 예보버전조회
    numOfRowsFcstVersion = "1"
     **/


    private val valueCategory = "category"
    private val valueTypeObsrValue = "obsrValue"
    private val valueTypeFcstValue = "fcstValue"

    val onceJO = JSONObject()
    private lateinit var realDate: String
    private lateinit var realTime: String

    // API 데이터 호출
    fun getWeatherData(
        method: String,
        numOfRows: String,
        x: String,
        y: String,
        yesterday: Boolean
    ) {
        try {
            realTime = millsToString(getCurrentTimeMills(), "HHmm")

            realTime = nearAlgorithm(realTime).split("_")[1]
            realDate = nearAlgorithm(realTime).split("_")[0]
            if (yesterday) {
                realDate = millsToString(getYesterdayLong(), "yyyyMMdd")
            }

            val strBuilder = weatherApiURL + method +  /*URL*/
                    "?" + encode("serviceKey", "UTF-8") +
                    "=" + encode(decodingKey, "UTF-8") +  //Service Key
                    "&" + encode("numOfRows", "UTF-8") +
                    "=" + encode(numOfRows, "UTF-8") + //한 페이지 결과 수
                    "&" + encode("pageNo", "UTF-8") +
                    "=" + encode("1", "UTF-8") +   //페이지번호
                    "&" + encode("dataType", "UTF-8") +
                    "=" + encode("JSON", "UTF-8") +   //데이터타입
                    "&" + encode("base_date", "UTF-8") +
                    "=" + encode(realDate, "UTF-8") +  //측정날짜
                    "&" + encode("base_time", "UTF-8") +
                    "=" + encode(realTime, "UTF-8") +  //정시단위시간
                    "&" + encode("nx", "UTF-8") +
                    "=" + encode(x, "UTF-8") +   //예보지점의 X 좌표값
                    "&" + encode("ny", "UTF-8") +
                    "=" + encode(y, "UTF-8") //예보지점의 Y 좌표값

            println(strBuilder)
            val url = URL(strBuilder)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Content-type", "application/json")
            println("Response code: " + conn.responseCode)
            val rd: BufferedReader = if (conn.responseMessage == "OK") {
                BufferedReader(InputStreamReader(conn.inputStream))
            } else {
                BufferedReader(InputStreamReader(conn.errorStream))
            }
            val sb = StringBuilder()
            var line: String?
            while (rd.readLine().also { line = it } != null) {
                sb.append(line)
            }
            rd.close()
            conn.disconnect()

            val responseJA = JSONObject(sb.toString()).getJSONObject("response")
                .getJSONObject("body").getJSONObject("items").getJSONArray("item")

            var fcstValue: String

            for (i: Int in 0 until (responseJA.length())) {
                val dataJO = responseJA.getJSONObject(i)
                val mCategory: String = if (yesterday) {
                    "y${dataJO.get(valueCategory)}"
                } else {
                    dataJO.get(valueCategory).toString()
                }
                if (method == "getVilageFcst") {
                    fcstValue = dataJO.get(valueTypeFcstValue).toString()
                    onceJO.put(mCategory, fcstValue)
                }
            }
        } catch (e: IOException) {
            Log.e("api_result", "IOException : ${e.localizedMessage}")
        } catch (e: InvocationTargetException) {
            Log.e("api_result", "InvocationTargetException : ${e.localizedMessage}")
        } catch (e: JSONException) {
            Log.e("api_result", "JSONException : ${e.localizedMessage}")
        }
    }

    // 근사값 알고리즘
    private fun nearAlgorithm(hour: String): String {
        // 단기예보 측정시간
        val array = arrayOf("0210", "0510", "0810", "1110", "1410", "1710", "2010", "2310")
        var time = ""
        var date = ""

        for (i: Int in 0 until (array.size)) {
            val diff = array[i].toInt() - hour.toInt()   // 측정시간 - 현재시간
            if (diff > 0) {  // 결과가 양수일 경우 = 오늘날짜 이전 측정 결과가 있는 경우
                if (i == 0) {
                    time = array.last()
                    date = millsToString(getYesterdayLong(), "yyyyMMdd")
                } else if (i == array.lastIndex) {
                    time = array.first()
                    date = millsToString(getYesterdayLong(), "yyyyMMdd")
                } else {
                    time = array[i - 1]   // 리턴값을 근삿값으로 지정
                    date = millsToString(getCurrentTimeMills(), "yyyyMMdd")
                    break
                }
            }
        }
        Log.d(TAG_R, "answer :${date}_${time}")

        return "${date}_${time}"
    }
}