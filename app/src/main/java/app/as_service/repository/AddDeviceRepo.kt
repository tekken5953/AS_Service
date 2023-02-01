package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.CODE_SERVER_DOWN
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.dao.StaticDataObject.httpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddDeviceRepo {
    var _postDeviceResultData = MutableLiveData<String>()    // 회원가입 Response Body : String(Ok Sign)

    init {
        httpClient.getInstance()    // HTTP Client 인스턴스 생성
    }

    fun loadPostDeviceResult(token: String, device: String, id: String, name: String, business: String) {
        val postDeviceModel = ApiModel.PostDevice(device, id, name, business)

        httpClient.mMyAPI.postDevice(token, postDeviceModel).run {
            enqueue(object : Callback<ApiModel.ReturnPost> {
                override fun onResponse(
                    call: Call<ApiModel.ReturnPost>,
                    response: Response<ApiModel.ReturnPost>
                ) {
                    Log.d(TAG,"Response body is ${response.body().toString()}")
                    if (response.code() == 200) {
                        Log.d(TAG, "장치등록 성공 : ${response.code()}")
                        _postDeviceResultData.value = response.code().toString()
                    } else if (response.code() == CODE_SERVER_DOWN) {
                        Log.e(TAG, "서버 연결 불가 : ${response.code()}")
                        _postDeviceResultData.value = CODE_SERVER_DOWN.toString()
                    } else {
                        Log.w(TAG, "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
                        _postDeviceResultData.value = RESPONSE_DEFAULT
                    }
                }

                override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
                    Log.e(TAG, "장치등록 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}