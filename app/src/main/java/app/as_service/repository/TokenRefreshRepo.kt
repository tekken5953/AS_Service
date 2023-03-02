package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.CODE_INVALID_TOKEN
import app.as_service.dao.StaticDataObject.CODE_SERVER_DOWN
import app.as_service.dao.StaticDataObject.CODE_SERVER_OK
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.dao.StaticDataObject.TAG_R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenRefreshRepo : BaseRepository() {
    var _refreshTokenResultData = MutableLiveData<List<String>>()

    fun loadRefreshTokenResult(access: String, refresh: String) {

        httpClient.mMyAPI.refreshToken(access, ApiModel.RefreshToken(refresh)).run {
            enqueue(object : Callback<ApiModel.LoginToken> {
                override fun onResponse(
                    call: Call<ApiModel.LoginToken>,
                    response: Response<ApiModel.LoginToken>
                ) {
                    Log.d(TAG_R, "토큰갱신 바디 : ${response.body().toString()}")
                    when (response.code()) {
                        CODE_SERVER_OK -> {
                            val newAccess = response.body()?.access.toString()
                            val newRefresh = response.body()?.refresh.toString()
                            _refreshTokenResultData.value = listOf(newAccess,newRefresh)
                        }
                        CODE_SERVER_DOWN -> {
                            Log.e(TAG_R, "서버 연결 불가 : ${response.code()}")
                            _refreshTokenResultData.value = listOf(RESPONSE_DEFAULT)
                        }
                        CODE_INVALID_TOKEN -> {
                            Log.w(TAG_R, "만료된 토큰 : ${response.code()}")
                            _refreshTokenResultData.value = listOf(CODE_INVALID_TOKEN.toString())
                        }
                        else -> {
                            Log.w(TAG_R, "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
                            _refreshTokenResultData.value = listOf(RESPONSE_DEFAULT)
                        }
                    }
                }

                override fun onFailure(call: Call<ApiModel.LoginToken>, t: Throwable) {
                    Log.e(TAG_R, "토큰갱신 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}