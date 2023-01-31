package app.as_service.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.repository.GetValueRepo

class GetValueDataModel : ViewModel() {

    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var getDataResultData: LiveData<ApiModel.GetData>
    private val repo = GetValueRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadDataResult(sn: String, token: String) {
        repo.loadDataResult(sn, token)
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getDataResult(): LiveData<ApiModel.GetData> {
        getDataResultData = repo._getDataResult
        return getDataResultData
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "데이터 호출 뷰모델 인스턴스 소멸")
    }
}