package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.StaticDataObject.CODE_SERVER_DOWN
import app.as_service.dao.StaticDataObject.CODE_SERVER_OK
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.server.HttpClient
import retrofit2.Response

open class BaseRepository {
    val httpClient = HttpClient

    init {
        httpClient.getInstance()
    }

    inline fun <reified T> loadSuccessStringData (
        data: MutableLiveData<String>,
        response: Response<T>,
        title: String
    ) {
        Log.d(TAG_R, "$title 바디 : ${response.body().toString()}")
            when (response.code()) {
                CODE_SERVER_OK -> {
                    data.value = CODE_SERVER_OK.toString()
                }
                CODE_SERVER_DOWN -> {
                    Log.e(TAG_R, "서버 연결 불가 : ${response.code()}")
                    data.value = CODE_SERVER_DOWN.toString()
                }
                else -> {
                    Log.w(TAG_R, "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
                    data.value = RESPONSE_DEFAULT
                }
        }
    }

    inline fun <reified TD, TR> loadSuccessMapData (
        data: MutableLiveData<TD>,
        response: Response<TR>,
        title: String
    ) {
        Log.d(TAG_R, "$title 바디 : ${response.body().toString()}")
        when (response.code()) {
            CODE_SERVER_OK -> {
                data.value = response.body() as TD
            }
            CODE_SERVER_DOWN -> {
                Log.e(TAG_R, "서버 연결 불가 : ${response.code()}")
            }
            else -> {
                Log.w(TAG_R, "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
            }
        }
    }

    inline fun <reified TD, TR> loadSuccessListData(
        data: MutableLiveData<TD>,
        response: Response<TR>,
        title: String
    ) {
        Log.d(TAG_R, "$title 바디 : ${response.body().toString()}")
        when (response.code()) {
            CODE_SERVER_OK -> {
                val mList: TR? = response.body()
                data.value = mList as TD
            }
            CODE_SERVER_DOWN -> {
                Log.e(TAG_R, "서버 연결 불가 : ${response.code()}")
            }
            else -> {
                Log.w(TAG_R, "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
            }
        }
    }
}