package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.AdapterModel
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.server.HttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("PropertyName")
class DeviceListRepo : BaseRepository() {
    var _deviceListResult =
        MutableLiveData<List<AdapterModel.GetDeviceList>?>()

    fun loadDeviceListResult(access: String) {
        val getDeviceList: Call<List<AdapterModel.GetDeviceList>> =
            HttpClient.mMyAPI.getDeviceList(access)
        getDeviceList.enqueue(object : Callback<List<AdapterModel.GetDeviceList>> {
            override fun onResponse(
                call: Call<List<AdapterModel.GetDeviceList>>,
                response: Response<List<AdapterModel.GetDeviceList>>
            ) {
                loadSuccessListData(_deviceListResult,response,"디바이스 리스트 호출")
            }

            override fun onFailure(call: Call<List<AdapterModel.GetDeviceList>>, t: Throwable) {
                Log.e(TAG_R,"디바이스 리스트 불러오기 실패 : ${t.localizedMessage}")
            }
        })
    }
}