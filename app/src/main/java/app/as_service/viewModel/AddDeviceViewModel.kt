package app.as_service.viewModel

import androidx.lifecycle.LiveData
import app.as_service.repository.AddDeviceRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddDeviceViewModel : BaseViewModel("장치등록") {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var postDeviceResult: LiveData<String>
    private val repo = AddDeviceRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadPostDeviceResult(token: String, device: String, id: String, name: String, business: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            repo.loadPostDeviceResult(token, device, id, name, business)
        }
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun postDeviceResult(): LiveData<String> {
        postDeviceResult = repo._postDeviceResultData
        return postDeviceResult
    }
}