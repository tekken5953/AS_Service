package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.TAG_R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("PropertyName")
class SignUpRepo : BaseRepository() {
    var _signUpResultData = MutableLiveData<String>()    // 회원가입 Response Body : String(Ok Sign)

    fun loadSignUpResult(username: String, phone: String, password: String) {
        val signUpModel = ApiModel.Member(username, phone, password)

        httpClient.mMyAPI.postSignUp(signUpModel).run {
            enqueue(object : Callback<ApiModel.ReturnPost> {
                override fun onResponse(
                    call: Call<ApiModel.ReturnPost>,
                    response: Response<ApiModel.ReturnPost>
                ) {
                   loadSuccessStringData(_signUpResultData,response,"회원가입")
                }
                override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
                    Log.e(TAG_R, "로그인 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}