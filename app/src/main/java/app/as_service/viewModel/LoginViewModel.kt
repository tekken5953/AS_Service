package app.as_service.viewModel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.repository.LoginRepo
import org.koin.java.KoinJavaComponent.inject

class LoginViewModel : ViewModel() {
    // MutableLiveData 값을 받아 View 로 전달해 줄 LiveData
    private lateinit var signInResultData: LiveData<String>
    private val repo = LoginRepo()

    // MutableLiveData 값을 갱신하기 위한 함수
    fun loadSignInResult(username: String, password: String) {
        repo.loadSignInResult(username, password)
    }

    // LiveData 에 MutableLiveData 값 적용 후 View 에 전달
    fun getSignInResult(): LiveData<String> {
        signInResultData = repo._signInResultData
        return signInResultData
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "로그인 뷰모델 인스턴스 소멸")
    }
}