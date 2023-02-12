package app.as_service.viewModel

import androidx.lifecycle.LiveData
import app.as_service.repository.LoginRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel("로그인") {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var signInResultData: LiveData<String>
    private val repo = LoginRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadSignInResult(username: String, password: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            repo.loadSignInResult(username, password)
        }
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getSignInResult(): LiveData<String> {
        signInResultData = repo._signInResultData
        return signInResultData
    }
}