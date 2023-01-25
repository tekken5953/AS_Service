package app.as_service.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.as_service.dao.StaticDataObject.signUpRepository

class SignUpViewModel : ViewModel() {
    private val signUpResultData: LiveData<String>
        get() = signUpRepository._signUpResultData

    fun loadSignUpResult() {
        signUpRepository.loadSignUpResult()
    }

    fun getSignUpCode() : LiveData<String> {
        return signUpResultData
    }
}