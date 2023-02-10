package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.server.HttpClient.mMyAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("PropertyName")
class GetValueRepo : BaseRepository() {
    // 공기질 데이터 호출 Response Body : Map
    var _getDataResult =
        MutableLiveData<ApiModel.GetData>()

    fun loadDataResult(sn: String, token: String) {
        val getDataMap: Call<ApiModel.GetData> = mMyAPI.getData(sn, token)
        getDataMap.enqueue(object : Callback<ApiModel.GetData> {
            override fun onResponse(
                call: Call<ApiModel.GetData>,
                response: Response<ApiModel.GetData>
            ) {
                loadSuccessMapData(_getDataResult, response, "공기질 데이터")
            }
            override fun onFailure(call: Call<ApiModel.GetData>, t: Throwable) {
                Log.e(TAG_R, "공기질 데이터 호출 실패 : ${t.localizedMessage}")
            }
        })
    }
}