package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.dao.StaticDataObject.httpClient
import app.as_service.server.HttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeviceListRepo {
    var _deviceListResult =
        MutableLiveData<ArrayList<ApiModel.GetDeviceList>?>()    // 로그인 Response Body : access token

    init {
        httpClient.getInstance()    // HTTP Client 인스턴스 생성
    }

    fun loadDeviceListResult(token: String) {
        val getDeviceList: Call<List<ApiModel.GetDeviceList>> =
            HttpClient.mMyAPI.getDeviceList(token)
        getDeviceList.enqueue(object : Callback<List<ApiModel.GetDeviceList>> {
            override fun onResponse(
                call: Call<List<ApiModel.GetDeviceList>>,
                response: Response<List<ApiModel.GetDeviceList>>
            ) {
                if (response.code() == 200) {
                    Log.d(TAG, "디바이스 리스트 불러오기 성공 : ${response.code()}")
                    Log.d(TAG,"바디 : ${response.body().toString()}")
                    val mList: List<ApiModel.GetDeviceList>? = response.body()
                    _deviceListResult.value = mList as ArrayList<ApiModel.GetDeviceList>?
                } else {
                    Log.w(TAG, "디바이스 리스트를 불러왔지만 오류발생 : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ApiModel.GetDeviceList>>, t: Throwable) {
                Log.e(TAG,"디바이스 리스트 불러오기 실패 : ${t.localizedMessage}")
            }
        })
    }
}