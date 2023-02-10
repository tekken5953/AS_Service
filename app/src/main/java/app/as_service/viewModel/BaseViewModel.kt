package app.as_service.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import app.as_service.dao.StaticDataObject.TAG_R
import kotlinx.coroutines.Job

open class BaseViewModel(msg: String) : ViewModel() {
    var message = msg
    var job: Job? = null

    override fun onCleared() {
        super.onCleared()
        if (job != null)
            job?.cancel()
        Log.d(TAG_R, "$message 뷰모델 인스턴스 소멸")
    }
}