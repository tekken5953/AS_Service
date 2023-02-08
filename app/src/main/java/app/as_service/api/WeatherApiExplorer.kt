package app.as_service.api

import android.util.Log
import app.as_service.dao.IgnoredKeyFile.decodingKey
import app.as_service.dao.IgnoredKeyFile.weatherApiURL
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.dao.StaticDataObject.RESPONSE_FAIL
import app.as_service.repository.BaseRepository
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class WeatherApiExplorer : BaseRepository() {

    val getUltraSrtNcst = "getUltraSrtNcst" // 초단기실황조회
    val numOfUltraSrtNcst = "8"
    val getUltraSrtFcst = "getUltraSrtFcst" // 초단기예보조회
    val numOfUltraSrtFcst = "10"
    val getVilageFcst = "getVilageFcst" // 단기예보조회
    val numOfRowsVilageFcst = "14"
    val getFcstVersion = "getFcstVersion" // 예보버전조회
    val numOfRowsFcstVersion = "1"

    private val valueCategory = "category"
    private val valueTypeObsrValue = "obsrValue"
    private val valueTypeFcstValue = "fcstValue"
    private val valueTypeFcstDate = "fcstDate"
    private val valueTypeFcstTime = "fcstTime"
    private val valueTypeBaseDate = "baseDate"
    private val valueTypeBaseTime = "baseTime"

    // API 데이터 호출
    fun getWeatherData(method: String, numOfRows: String, x: String, y: String, date: String, time: String): String {
        try {
            // 측정시간이 대충 30분 정도 되는 것 같음. 30분 이전에는 No Data를 반환하니 한시간 이전 데이터를 표출함
            var realTime: String = time
            if (method != getVilageFcst) {
                if (realTime.substring(2, 4).toInt() <= 30) {
                    realTime = if ((realTime.toInt() - 100).toString().length == 3) {
                        "0${time.toInt() - 100}"
                    } else {
                        (time.toInt() - 100).toString()
                    }
                }
            } else {
                realTime = if ((realTime.toInt() - 300).toString().length == 3) {
                    "0${time.toInt() - 300}"
                } else {
                    (time.toInt() - 300).toString()
                }
            }


            val strBuilder = weatherApiURL  + method +  /*URL*/
                    "?" + URLEncoder.encode("serviceKey", "UTF-8") +
                    "=" + URLEncoder.encode(decodingKey, "UTF-8") +  //Service Key
                    "&" + URLEncoder.encode("numOfRows", "UTF-8") +
                    "=" + URLEncoder.encode(numOfRows, "UTF-8") + //한 페이지 결과 수
                    "&" + URLEncoder.encode("pageNo", "UTF-8") +
                    "=" + URLEncoder.encode("1", "UTF-8") +   //페이지번호
                    "&" + URLEncoder.encode("dataType", "UTF-8") +
                    "=" + URLEncoder.encode("JSON", "UTF-8") +   //데이터타입
                    "&" + URLEncoder.encode("base_date", "UTF-8") +
                    "=" + URLEncoder.encode(date, "UTF-8") +  //측정날짜
                    "&" + URLEncoder.encode("base_time", "UTF-8") +
                    "=" + URLEncoder.encode(realTime, "UTF-8") +  //정시단위시간
                    "&" + URLEncoder.encode("nx", "UTF-8") +
                    "=" + URLEncoder.encode(x, "UTF-8") +   //예보지점의 X 좌표값
                    "&" + URLEncoder.encode("ny", "UTF-8") +
                    "=" + URLEncoder.encode(y, "UTF-8") //예보지점의 Y 좌표값
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

            var resultObsrIndex = ""
            var resultFcstIndex = ""
            var resultBaseDate = ""
            var resultBaseTime = ""
            var resultFcstDate = ""
            var resultFcstTime = ""
            var obsrValue=""
            var fcstValue = ""

            for (i: Int in 0 until (responseJA.length())) {
                val dataJO = responseJA.getJSONObject(i)
                val category = convertCategoryToString(dataJO.get(valueCategory).toString())
                resultBaseDate = dataJO.get(valueTypeBaseDate).toString()
                resultBaseTime = dataJO.get(valueTypeBaseTime).toString()
                if (method == getUltraSrtNcst) {
                    obsrValue = dataJO.get(valueTypeObsrValue).toString()
                    if (category != "FAIL")
                        resultObsrIndex += "$category : $obsrValue\n\n"
                } else {
                    fcstValue = dataJO.get(valueTypeFcstValue).toString()
                    resultFcstDate = dataJO.get(valueTypeFcstDate).toString()
                    resultFcstTime = dataJO.get(valueTypeFcstTime).toString()
                    if (category != "FAIL")
                      resultFcstIndex += "$category : $fcstValue\n\n"
                }
            }
            return if (method == getUltraSrtNcst) {
                "측정 시간 : ${convertBaseDateToFormatDate(resultBaseDate)} " +
                        "${convertBaseTimeToFormatTime(resultBaseTime)}\n\n$resultObsrIndex"
            } else {
                "측정 시간 : ${convertBaseDateToFormatDate(resultBaseDate)} " +
                        "${convertBaseTimeToFormatTime(resultBaseTime)}\n\n" +
                        "예상 시간 : ${convertBaseDateToFormatDate(resultFcstDate)} " +
                        "${convertBaseTimeToFormatTime(resultFcstTime)}\n\n$resultFcstIndex"
            }
        } catch (e: IOException) {
            Log.e("api_result", "IOException : ${e.localizedMessage}")
            return RESPONSE_DEFAULT
        } catch (e: JSONException) {
            Log.e("api_result", "JSONException : ${e.localizedMessage}")
            return RESPONSE_FAIL
        }
    }

    // 현재는 초단기실황조회에 해당하는 컬럼명만 변환함
    private fun convertCategoryToString(category: String) : String {
        when (category) {
            "PTY" -> return "강수형태 - \n없음(0), 비(1), 비/눈(2), 눈(3)\n빗방울(5), 빗방울눈날림(6), 눈날림(7)\n"
            "T1H" -> return "기온(℃)"
            "RN1" -> return "1시간 강수량(mm)"
            "UUU" -> return "동서바람성분(m/s)"
            "VVV" -> return "남북바람성분(m/s)"
            "REH" -> return "습도(%)"
            "WSD" -> return "풍속(m/s)"
            "VEC" -> return "풍향(deg)"
            "POP" -> return "강수확률(%)"
            "PCP" -> return "1시간 강수량(mm)"
            "SNO" -> return "1시간 신적설(cm)"
            "SKY" -> return "하늘상태 - \n맑음(1), 구름많음(3), 흐림(4)\n"
            "TMP" -> return "1시간 기온(℃)"
            "TMN" -> return "일 최저기온(℃)"
            "TMX" -> return "일 최고기온(℃)"
            "WAV" -> return "파고(M)"
        }
        return "FAIL"
    }

    // 날짜 형식을 변환
    private fun convertBaseDateToFormatDate(baseDate: String) : String {
            return "${baseDate.substring(0,4)}년 ${baseDate.substring(4,6)}월 ${baseDate.substring(6,baseDate.length)}일"
    }

    // 시간 형식을 변환
    private fun convertBaseTimeToFormatTime(baseTime: String) : String {
        return "${baseTime.substring(0,2)}시 ${baseTime.substring(2,baseTime.length)}분"
    }
}