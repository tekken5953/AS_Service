package app.as_service.viewModel

import androidx.lifecycle.LiveData
import app.as_service.repository.RefreshTokenRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RefreshTokenViewModel : BaseViewModel("토큰 갱신") {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var refreshTokenResultData: LiveData<String>
    private val repo = RefreshTokenRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadSignUpResult(access: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            repo.loadSignUpResult(access)
        }
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getSignUpCode() : LiveData<String> {
        refreshTokenResultData = repo._refreshTokenResultData
        return refreshTokenResultData
    }
}