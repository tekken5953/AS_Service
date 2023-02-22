package app.as_service.viewModel

import androidx.lifecycle.LiveData
import app.as_service.dao.ApiModel
import app.as_service.repository.TokenRefreshRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TokenRefreshViewModel : BaseViewModel("토큰 갱신") {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var refreshTokenResultData: LiveData<ApiModel.LoginToken>
    private val repo = TokenRefreshRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadRefreshResult(access: String, refresh: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            repo.loadRefreshTokenResult(access,refresh)
        }
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getResultToken() : LiveData<ApiModel.LoginToken> {
        refreshTokenResultData = repo._refreshTokenResultData
        return refreshTokenResultData
    }
}