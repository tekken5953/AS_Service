package app.as_service.api.weather

import android.util.Log
import app.as_service.dao.IgnoredKeyFile.WeatherDecodingKey
import app.as_service.dao.IgnoredKeyFile.weatherApiURL
import app.as_service.repository.BaseRepository
import app.as_service.util.ConvertDataTypeUtil.getCurrentTimeMills
import app.as_service.util.ConvertDataTypeUtil.getYesterdayLong
import app.as_service.util.ConvertDataTypeUtil.millsToString
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.orhanobut.logger.Logger
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
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

            realTime = NearAlgorithm().execute(realTime).split("_")[1]
            realDate = NearAlgorithm().execute(realTime).split("_")[0]
            if (yesterday) {
                realDate = millsToString(getYesterdayLong(), "yyyyMMdd")
            }

            val strBuilder = weatherApiURL + method +  /*URL*/
                    "?" + encode("serviceKey", "UTF-8") +
                    "=" + encode(WeatherDecodingKey, "UTF-8") +  //Service Key
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
            Timber.tag("Weather").d(
                GsonBuilder().setPrettyPrinting().create().toJson(
                    JsonParser().parse(responseJA.toString())
                )
            )

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
            Logger.e("api_result", "IOException : ${e.localizedMessage}")
        } catch (e: InvocationTargetException) {
            Logger.e("api_result", "InvocationTargetException : ${e.localizedMessage}")
        } catch (e: JSONException) {
            Logger.e("api_result", "JSONException : ${e.localizedMessage}")
        }
    }


}