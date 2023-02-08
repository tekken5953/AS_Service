package app.as_service.viewModel

import androidx.lifecycle.LiveData
import app.as_service.dao.ApiModel
import app.as_service.repository.GetValueRepo

class GetValueViewModel : BaseViewModel("데이터 호출") {

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
}