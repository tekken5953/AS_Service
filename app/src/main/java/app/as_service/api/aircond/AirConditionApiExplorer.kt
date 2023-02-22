package app.as_service.api.aircond

import android.util.Log
import app.as_service.dao.IgnoredKeyFile
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.repository.BaseRepository
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class AirConditionApiExplorer : BaseRepository() {
    /**
     * getMsrstnAcctoRltmMesureDnsty  - 축정소별 실시간 측정정보 조회
     * getUnityAirEnvrnIdexSnstiveAboveMsrstnList   -   통합대기환경지수 나쁨 이상 측정소 목록조회
     * getCtprvnRltmMesureDnsty -   시도별 실시간 측정정보 조회
     * getMinuDustFrcstDspth    -   대기질 예보통보 조회
     * getMinuDustWeekFrcstDspth    -   초미세먼지 주간예보 조회
     */

    val dataJO = JSONObject()

    fun getAirData(
        dataTerm: String,
        stationName: String,
        ver: String
    ) {
        val strBuilder = IgnoredKeyFile.airCondApiURL + "getMsrstnAcctoRltmMesureDnsty" +  /*URL*/
                "?" + URLEncoder.encode("stationName", "UTF-8") +
                "=" + stationName +  //측정소 이름
                "&" + URLEncoder.encode("dataTerm", "UTF-8") +
                "=" + URLEncoder.encode(dataTerm, "UTF-8") +  //요청 데이터기간(1일: DAILY, 1개월: MONTH, 3개월: 3MONTH)
                "&" + URLEncoder.encode("pageNo", "UTF-8") +
                "=" + URLEncoder.encode("1", "UTF-8") +   //페이지번호
                "&" + URLEncoder.encode("numOfRows", "UTF-8") +
                "=" + URLEncoder.encode("1", "UTF-8") + //한 페이지 결과 수
                "&" + URLEncoder.encode("returnType", "UTF-8") +
                "=" + URLEncoder.encode("json", "UTF-8") +   //데이터타입
                "&" + URLEncoder.encode("serviceKey", "UTF-8") +
                "=" + URLEncoder.encode(IgnoredKeyFile.AirCondDecodingKey, "UTF-8") +  //Service Key
                "&" + URLEncoder.encode("ver", "UTF-8") +
                "=" + URLEncoder.encode(ver, "UTF-8")
                // 버전별 상세 결과(1.0: PM2.5포함, 1.1: PM10,2.5 24시간 예측이동 평균데이터 포함,
                // 1.2: 측정망 정보 데이터 포함, 1.3: PM10,2.5 한시간 등급 자료 포함
        Log.d(TAG_R, strBuilder)
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
            .getJSONObject("body").getJSONArray("items")
        val data = responseJA.getJSONObject(0).get("pm10Value").toString()
        dataJO.put("pm10Value",data)
    }
}