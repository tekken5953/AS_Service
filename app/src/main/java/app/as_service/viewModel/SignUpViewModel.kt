package app.as_service.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.repository.SignUpRepo

class SignUpViewModel : ViewModel() {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var signUpResultData: LiveData<String>
    private val repo = SignUpRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadSignUpResult(username: String,phone: String,password: String) {
        repo.loadSignUpResult(username,phone, password)
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getSignUpCode() : LiveData<String> {
        signUpResultData = repo._signUpResultData
        return signUpResultData
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "회원가입 뷰모델 인스턴스 소멸")
    }
}