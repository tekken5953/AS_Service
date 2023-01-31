package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.dao.StaticDataObject.httpClient
import app.as_service.server.HttpClient.mMyAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetValueRepo {
    // 공기질 데이터 호출 Response Body : Map
    var _getDataResult =
        MutableLiveData<ApiModel.GetData>()

    init {
        httpClient.getInstance()    // HTTP Client 인스턴스 생성
    }

    fun loadDataResult(sn: String, token: String) {
        val getDataMap: Call<ApiModel.GetData> = mMyAPI.getData(sn, token)
        getDataMap.enqueue(object : Callback<ApiModel.GetData> {
            override fun onResponse(
                call: Call<ApiModel.GetData>,
                response: Response<ApiModel.GetData>
            ) {
                if (response.code() == 200) {
                    Log.d(TAG, "공기질 데이터 : ${response.body()}")
                    _getDataResult.value = response.body()
                }
            }
            override fun onFailure(call: Call<ApiModel.GetData>, t: Throwable) {
                Log.e(TAG, "공기질 데이터 호출 실패 : ${t.localizedMessage}")
            }
        })
    }
}