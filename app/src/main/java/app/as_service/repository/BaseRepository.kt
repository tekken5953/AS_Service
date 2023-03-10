package app.as_service.repository

import androidx.lifecycle.MutableLiveData
import app.as_service.dao.StaticDataObject.CODE_INVALID_TOKEN
import app.as_service.dao.StaticDataObject.CODE_SERVER_DOWN
import app.as_service.dao.StaticDataObject.CODE_SERVER_OK
import app.as_service.server.HttpClient
import com.orhanobut.logger.Logger
import retrofit2.Response

open class BaseRepository {
    val httpClient = HttpClient

    init {
        httpClient.getInstance()
    }

    inline fun <reified T> loadSuccessStringData(
        data: MutableLiveData<String>,
        response: Response<T>,
    ) {
        when (response.code()) {
            CODE_SERVER_OK -> {
                data.value = CODE_SERVER_OK.toString()
            }
            CODE_SERVER_DOWN -> {
                Logger.e("서버 연결 불가 : ${response.code()}")
            }
            CODE_INVALID_TOKEN -> {
                Logger.w("만료된 토큰 : ${response.code()}")
            }
            else -> {
                Logger.w("통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
            }
        }
    }

    inline fun <reified TD, TR> loadSuccessMapData(
        data: MutableLiveData<TD>,
        response: Response<TR>
    ) {
        when (response.code()) {
            CODE_SERVER_OK -> {
                data.value = response.body() as TD
            }
            CODE_SERVER_DOWN -> {
                Logger.e("서버 연결 불가 : ${response.code()}")
            }
            CODE_INVALID_TOKEN -> {
                Logger.w("만료된 토큰 : ${response.code()}")
            }
            else -> {
                Logger.w("통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
            }
        }
    }

    inline fun <reified TD, TR> loadSuccessListData(
        data: MutableLiveData<TD>,
        response: Response<TR>,
    ) {
        val mList: TR? = response.body()
        when (response.code()) {
            CODE_SERVER_OK -> {
                data.value = mList as TD
            }
            CODE_SERVER_DOWN -> {
                Logger.e("서버 연결 불가 : ${response.code()}")
            }
            CODE_INVALID_TOKEN -> {
                Logger.w("만료된 토큰 : ${response.code()}")
                data.value = null
            }
            else -> {
                Logger.w("통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
            }
        }
    }
}