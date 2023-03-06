package app.as_service.repository

import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import com.orhanobut.logger.Logger
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
                   loadSuccessStringData(_deleteDeviceResultData,response)
                }

                override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
                    Logger.e("장치삭제 실패 : " + t.localizedMessage)
                }
            })
        }
    }
}