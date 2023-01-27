package app.as_service.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.repository.DeviceListRepo

class DeviceListViewModel : ViewModel() {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var deviceListResult: LiveData<ArrayList<ApiModel.GetDeviceList>?>
    private val repo = DeviceListRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadDeviceListResult(token: String) {
        repo.loadDeviceListResult(token)
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getDeviceListResult(): LiveData<ArrayList<ApiModel.GetDeviceList>?> {
        deviceListResult = repo._deviceListResult
        return deviceListResult
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "디바이스 리스트 뷰모델 인스턴스 소멸")
    }
}