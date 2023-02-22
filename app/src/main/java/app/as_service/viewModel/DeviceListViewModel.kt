package app.as_service.viewModel

import androidx.lifecycle.LiveData
import app.as_service.dao.AdapterModel
import app.as_service.repository.DeviceListRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceListViewModel : BaseViewModel("디바이스 리스트") {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var deviceListResult: LiveData<List<AdapterModel.GetDeviceList>?>
    private val repo = DeviceListRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadDeviceListResult(access: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            repo.loadDeviceListResult(access)
        }
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getDeviceListResult(): LiveData<List<AdapterModel.GetDeviceList>?> {
        deviceListResult = repo._deviceListResult
        return deviceListResult
    }
}