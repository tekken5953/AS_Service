package app.as_service.viewModel

import androidx.lifecycle.LiveData
import app.as_service.dao.ApiModel
import app.as_service.repository.BookMarkRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookMarkViewModel : BaseViewModel("북마크") {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var patchBookMarkResult: LiveData<String>
    private val repo = BookMarkRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadPatchBookMarkResult(sn: String, token: String, field: ApiModel.PutBookMark) {
        job = CoroutineScope(Dispatchers.IO).launch {
            repo.loadPatchBookMarkResult(sn, token, field)
        }
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun patchBookMarkResult(): LiveData<String> {
        patchBookMarkResult = repo._patchBookMarkResultData
        return patchBookMarkResult
    }
}