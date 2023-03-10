package app.as_service.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
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

    /**
     * @param tag Generate Tag with Timber
     * @param json  Write JsonString for Parsing with PrettyPrinting
     */
    fun logJsonTimberDebug(tag: String, json: String) {
        Timber.tag(tag).d(
            GsonBuilder().setPrettyPrinting().create().toJson(
                JsonParser().parse(json)
            )
        )
    }

    fun logJsonTimberInfo(tag: String, json: String) {
        Timber.tag(tag).i(
            GsonBuilder().setPrettyPrinting().create().toJson(
                JsonParser().parse(json)
            )
        )
    }

    fun logJsonTimberWarning(tag: String, json: String) {
        Timber.tag(tag).w(
            GsonBuilder().setPrettyPrinting().create().toJson(
                JsonParser().parse(json)
            )
        )
    }

    fun logJsonTimberError(tag: String, json: String) {
        Timber.tag(tag).e(
            GsonBuilder().setPrettyPrinting().create().toJson(
                JsonParser().parse(json)
            )
        )
    }
}