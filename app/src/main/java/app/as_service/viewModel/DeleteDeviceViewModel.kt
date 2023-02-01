package app.as_service.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.as_service.dao.StaticDataObject
import app.as_service.repository.DeleteDeviceRepo

class DeleteDeviceViewModel : ViewModel() {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var deleteDeviceResult: LiveData<String>
    private val repo = DeleteDeviceRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadPostDeviceResult(token: String, device: String) {
        repo.loadDeleteDeviceResult(token, device)
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun deleteDeviceResult(): LiveData<String> {
        deleteDeviceResult = repo._deleteDeviceResultData
        return deleteDeviceResult
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(StaticDataObject.TAG, "장치삭제 뷰모델 인스턴스 소멸")
    }
}