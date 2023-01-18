package app.as_service.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.as_service.dao.StaticDataObject.loginRepository

class LoginViewModel : ViewModel() {
    private lateinit var signInResultData: LiveData<String> // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadSignInResult(username: String, password: String) {
        loginRepository.loadSignInResult(username, password)
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getSignInResult(): LiveData<String> {
        signInResultData = loginRepository._signInResultData
        return signInResultData
    }
}