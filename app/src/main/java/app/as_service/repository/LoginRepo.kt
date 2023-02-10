package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.CODE_SERVER_DOWN
import app.as_service.dao.StaticDataObject.CODE_SERVER_OK
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.dao.StaticDataObject.RESPONSE_FAIL
import app.as_service.dao.StaticDataObject.TAG_R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("PropertyName")
class LoginRepo : BaseRepository() {
    var _signInResultData = MutableLiveData<String>()    // 로그인 Response Body : access token

    fun loadSignInResult(username: String, password: String) {
        val loginModel = ApiModel.Login(username, password)
        httpClient.mMyAPI.postUsers(loginModel).run {
            enqueue(object : Callback<ApiModel.AccessToken> {
                override fun onResponse(
                    call: Call<ApiModel.AccessToken>,
                    response: Response<ApiModel.AccessToken>
                ) {
                    // API 통신 성공
                    if (response.code() == CODE_SERVER_OK) {
                        val token =
                            response.body()?.access.toString()  // response body 에 전달 된 access Json 으로 값 갱신
                        Log.d(TAG_R, "로그인 성공 : ${response.code()}")
                        Log.d(TAG_R, "엑세스 토큰 발행(Response Body) : \n$token")
                        _signInResultData.value = token // MutableLiveData 값 갱신
                    }
                    // 서버 연결 실패(404)
                    else if (response.code() == CODE_SERVER_DOWN) {
                        Log.e(TAG_R, "서버 연결 불가 : ${response.code()}")
                        _signInResultData.value = RESPONSE_DEFAULT
                    }
                    // 나머지 에러코드(추가예정)
                    else {
                        Log.w(TAG_R, "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
                        _signInResultData.value = RESPONSE_DEFAULT
                    }
                }

                // API 통신 실패
                override fun onFailure(call: Call<ApiModel.AccessToken>, t: Throwable) {
                    _signInResultData.value = RESPONSE_FAIL
                    Log.e(TAG_R, "로그인 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}