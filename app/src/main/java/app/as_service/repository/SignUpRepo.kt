package app.as_service.repository

import androidx.lifecycle.MutableLiveData
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.httpClient

class SignUpRepo {
    var _signUpResultData = MutableLiveData<String>()       // 회원가입 Response Body : String(Ok Sign)

    fun loadSignUpResult() {
        httpClient.getInstance()
        val code = StaticDataObject.RESPONSE_DEFAULT
        //TODO 서버연동 Instance 생성
        //TODO 회원가입 API 호출 후 MutableData ViewModel 로 전송
        _signUpResultData.value = code
    }
}