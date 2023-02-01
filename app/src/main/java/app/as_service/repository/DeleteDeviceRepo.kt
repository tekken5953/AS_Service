package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.httpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteDeviceRepo {
    var _deleteDeviceResultData = MutableLiveData<String>()    // 회원가입 Response Body : String(Ok Sign)

    init {
        httpClient.getInstance()    // HTTP Client 인스턴스 생성
    }

    fun loadDeleteDeviceResult(token: String, device: String) {
        httpClient.mMyAPI.deleteDevice(device, token).run {
            enqueue(object : Callback<ApiModel.ReturnPost> {
                override fun onResponse(
                    call: Call<ApiModel.ReturnPost>,
                    response: Response<ApiModel.ReturnPost>
                ) {
                    Log.d(StaticDataObject.TAG,"Response body is ${response.body().toString()}")
                    if (response.code() == 200) {
                        Log.d(StaticDataObject.TAG, "장치삭제 성공 : ${response.code()}")
                        _deleteDeviceResultData.value = response.code().toString()
                    } else if (response.code() == StaticDataObject.CODE_SERVER_DOWN) {
                        Log.e(StaticDataObject.TAG, "서버 연결 불가 : ${response.code()}")
                        _deleteDeviceResultData.value = StaticDataObject.CODE_SERVER_DOWN.toString()
                    } else {
                        Log.w(StaticDataObject.TAG, "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
                        _deleteDeviceResultData.value = StaticDataObject.RESPONSE_DEFAULT
                    }
                }

                override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
                    Log.e(StaticDataObject.TAG, "장치삭제 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}