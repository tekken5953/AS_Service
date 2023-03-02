package app.as_service.viewModel

import androidx.lifecycle.LiveData
import app.as_service.repository.SignUpRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel : BaseViewModel("회원가입") {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var signUpResultData: LiveData<String>
    private val repo = SignUpRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadSignUpResult(username: String, phone: String,password: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            repo.loadSignUpResult(username,phone, password)
        }
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getSignUpCode() : LiveData<String> {
        signUpResultData = repo._signUpResultData
        return signUpResultData
    }
}