package app.as_service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("PropertyName")
class DeleteDeviceRepo : BaseRepository() {
    var _deleteDeviceResultData = MutableLiveData<String>()    // 회원가입 Response Body : String(Ok Sign)

    fun loadDeleteDeviceResult(token: String, device: String) {
        httpClient.mMyAPI.deleteDevice(device, token).run {
            enqueue(object : Callback<ApiModel.ReturnPost> {
                override fun onResponse(
                    call: Call<ApiModel.ReturnPost>,
                    response: Response<ApiModel.ReturnPost>
                ) {
                   loadSuccessStringData(_deleteDeviceResultData,response,"장치삭제")
                }

                override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
                    Log.e(TAG, "장치삭제 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}