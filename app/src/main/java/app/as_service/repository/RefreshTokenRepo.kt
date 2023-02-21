package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.TAG_R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RefreshTokenRepo : BaseRepository() {
    var _refreshTokenResultData = MutableLiveData<String>()    // 회원가입 Response Body : String(Ok Sign)

    fun loadSignUpResult(access: String) {
        val refreshModel = ApiModel.RefreshToken(access)

        httpClient.mMyAPI.refreshToken(refreshModel).run {
            enqueue(object : Callback<ApiModel.AccessToken> {
                override fun onResponse(
                    call: Call<ApiModel.AccessToken>,
                    response: Response<ApiModel.AccessToken>
                ) {
                    loadSuccessStringData(_refreshTokenResultData,response,"토큰갱신")
                }
                override fun onFailure(call: Call<ApiModel.AccessToken>, t: Throwable) {
                    Log.e(TAG_R, "토큰갱신 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}