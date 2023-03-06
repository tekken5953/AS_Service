package app.as_service.repository

import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import com.orhanobut.logger.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("PropertyName")
class BookMarkRepo : BaseRepository() {
    var _patchBookMarkResultData = MutableLiveData<String>()    // 북마크 Response Body : String(Ok Sign)

    fun loadPatchBookMarkResult(sn: String, token: String, field: ApiModel.PutBookMark) {
        httpClient.mMyAPI.patchDevice(sn, token, field).run {
            enqueue(object : Callback<ApiModel.ReturnPost> {
                override fun onResponse(
                    call: Call<ApiModel.ReturnPost>,
                    response: Response<ApiModel.ReturnPost>
                ) { loadSuccessStringData(_patchBookMarkResultData, response) }

                override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
                    Logger.e("북마크 실패 : " + t.localizedMessage)
                }
            })
        }
    }
}