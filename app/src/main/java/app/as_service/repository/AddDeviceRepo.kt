package app.as_service.repository

import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import com.orhanobut.logger.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("PropertyName")
class AddDeviceRepo : BaseRepository() {
    var _postDeviceResultData = MutableLiveData<String>()    // 회원가입 Response Body : String(Ok Sign)

    fun loadPostDeviceResult(token: String, device: String, id: String, name: String, business: String) {
        val deviceModel = ApiModel.Device(device, id, name, business)

        httpClient.mMyAPI.postDevice(token, deviceModel).run {
            enqueue(object : Callback<ApiModel.ReturnPost> {
                override fun onResponse(
                    call: Call<ApiModel.ReturnPost>,
                    response: Response<ApiModel.ReturnPost>
                ) { loadSuccessStringData(_postDeviceResultData, response) }

                override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
                    Logger.e("장치등록 실패 : " + t.localizedMessage)
                }
            })
        }
    }
}