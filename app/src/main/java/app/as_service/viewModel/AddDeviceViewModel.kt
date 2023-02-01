package app.as_service.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.as_service.dao.StaticDataObject
import app.as_service.repository.AddDeviceRepo

class AddDeviceViewModel : ViewModel() {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var postDeviceResult: LiveData<String>
    private val repo = AddDeviceRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadPostDeviceResult(token: String, device: String, id: String, name: String, business: String) {
        repo.loadPostDeviceResult(token, device, id, name, business)
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun postDeviceResult(): LiveData<String> {
        postDeviceResult = repo._postDeviceResultData
        return postDeviceResult
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(StaticDataObject.TAG, "장치등록 뷰모델 인스턴스 소멸")
    }
}