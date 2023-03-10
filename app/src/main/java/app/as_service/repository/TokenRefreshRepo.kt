package app.as_service.repository

import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.CODE_INVALID_TOKEN
import app.as_service.dao.StaticDataObject.CODE_SERVER_DOWN
import app.as_service.dao.StaticDataObject.CODE_SERVER_OK
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.server.HttpClient.mMyAPI
import com.google.firebase.messaging.FirebaseMessaging.getInstance
import com.orhanobut.logger.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenRefreshRepo {
    var _refreshTokenResultData = MutableLiveData<List<String>>()

    fun loadRefreshTokenResult(access: String, refresh: String) {
        getInstance()

        mMyAPI.refreshToken(access, ApiModel.RefreshToken(refresh)).run {
            enqueue(object : Callback<ApiModel.LoginToken> {
                override fun onResponse(
                    call: Call<ApiModel.LoginToken>,
                    response: Response<ApiModel.LoginToken>
                ) {
                    when (response.code()) {
                        CODE_SERVER_OK -> {
                            Logger.d( "토큰갱신 바디 : ${response.body().toString()}")
                            val newAccess = response.body()?.access.toString()
                            val newRefresh = response.body()?.refresh.toString()
                            _refreshTokenResultData.value = listOf(newAccess,newRefresh)
                        }
                        CODE_SERVER_DOWN -> {
                            Logger.e( "서버 연결 불가 : ${response.code()}")
                            _refreshTokenResultData.value = listOf(RESPONSE_DEFAULT)
                        }
                        CODE_INVALID_TOKEN -> {
                            Logger.w( "만료된 토큰 : ${response.code()}")
                            _refreshTokenResultData.value = listOf(CODE_INVALID_TOKEN.toString())
                        }
                        else -> {
                            Logger.w( "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
                            _refreshTokenResultData.value = listOf(RESPONSE_DEFAULT)
                        }
                    }
                }

                override fun onFailure(call: Call<ApiModel.LoginToken>, t: Throwable) {
                    Logger.e( "토큰갱신 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}