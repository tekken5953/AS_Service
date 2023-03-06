package app.as_service.repository

import androidx.lifecycle.MutableLiveData
import app.as_service.dao.AdapterModel
import app.as_service.server.HttpClient
import com.orhanobut.logger.Logger
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
                loadSuccessListData(_deviceListResult,response)
            }

            override fun onFailure(call: Call<List<AdapterModel.GetDeviceList>>, t: Throwable) {
                Logger.e("디바이스 리스트 불러오기 실패 : " + t.localizedMessage)
            }
        })
    }
}