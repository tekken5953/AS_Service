package app.as_service.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import app.as_service.dao.StaticDataObject.TAG

open class BaseViewModel(msg: String) : ViewModel() {
    var message = msg

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "$message 뷰모델 인스턴스 소멸")
    }
}