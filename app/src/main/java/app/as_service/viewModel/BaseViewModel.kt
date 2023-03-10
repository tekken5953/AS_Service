package app.as_service.viewModel

import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Job

open class BaseViewModel(msg: String) : ViewModel() {
    var message = msg
    var job: Job? = null

    override fun onCleared() {
        super.onCleared()
        if (job != null)
            job?.cancel()
        Logger.i("$message 뷰모델 인스턴스 소멸")
    }
}