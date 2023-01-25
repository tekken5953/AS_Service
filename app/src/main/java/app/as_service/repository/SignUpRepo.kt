package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.CODE_SERVER_DOWN
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.dao.StaticDataObject.httpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpRepo {
    var _signUpResultData = MutableLiveData<String>()    // 회원가입 Response Body : String(Ok Sign)

    init {
        httpClient.getInstance()    // HTTP Client 인스턴스 생성
    }

    fun loadSignUpResult(username: String, phone: String, password: String) {
        val signUpModel = ApiModel.Member(username, phone, password)

        httpClient.mMyAPI.postSignUp(signUpModel).run {
            enqueue(object : Callback<ApiModel.ReturnPost> {
                override fun onResponse(
                    call: Call<ApiModel.ReturnPost>,
                    response: Response<ApiModel.ReturnPost>
                ) {
                    Log.d(TAG,"Response body is ${response.body().toString()}")
                    if (response.code() == 200) {
                        Log.d(TAG, "회원가입 성공 : ${response.code()}")
                        _signUpResultData.value = response.code().toString()
                    } else if (response.code() == CODE_SERVER_DOWN) {
                        Log.e(TAG, "서버 연결 불가 : ${response.code()}")
                        _signUpResultData.value = CODE_SERVER_DOWN.toString()
                    } else {
                        Log.w(TAG, "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
                        _signUpResultData.value = RESPONSE_DEFAULT
                    }
                }

                override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
                    Log.e(TAG, "로그인 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}