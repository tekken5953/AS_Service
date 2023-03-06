package app.as_service.util

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import timber.log.Timber

/**
 * @user : USER
 * @autor : Lee Jae Young
 * @since : 2023-03-06 오후 5:10
 * @version : 1.0.0
 **/
class LoggerUtil {
    fun getInstance() {
        Logger.addLogAdapter(AndroidLogAdapter())
        Timber.plant(Timber.DebugTree())
    }
}