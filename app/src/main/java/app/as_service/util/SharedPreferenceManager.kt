package app.as_service.util

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONException
import org.json.JSONObject

/**
 * @user : USER
 * @autor : Lee Jae Young
 * @since : 2023-03-07 오전 9:43
 * @version : 1.0.0
 **/

class SharedPreferenceManager(mContext: Context) {
    private val context = mContext
    private val PREFERENCES_NAME = "rebuild_preference"
    private val DEFAULT_VALUE_STRING = ""
    private val DEFAULT_VALUE_BOOLEAN = false
    private val DEFAULT_VALUE_INT = -1
    private val DEFAULT_VALUE_LONG = -1L
    private val DEFAULT_VALUE_FLOAT = -1f

    private fun getPreferences(): SharedPreferences {
        return context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    //String 값 저장
    fun setString(key: String, value: String) {
        val prefs = getPreferences()
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    //Boolean 값 저장
    fun setBoolean(key: String, value: Boolean) {
        val prefs = getPreferences()
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    //Integer 값 저장
    fun setInt(key: String, value: Int) {
        val prefs = getPreferences()
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    //Long 값 저장
    fun setLong(key: String, value: Long) {
        val prefs = getPreferences()
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    //Float 값 저장
    fun setFloat(key: String, value: Float) {
        val prefs = getPreferences()
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    //String 값 호출
    fun getString(key: String): String {
        val prefs =
            getPreferences()
        return prefs.getString(
            key,
            DEFAULT_VALUE_STRING
        )!!
    }

    //Boolean 값 호출
    fun getBoolean(key: String): Boolean {
        val prefs =
            getPreferences()
        return prefs.getBoolean(
            key,
            DEFAULT_VALUE_BOOLEAN
        )
    }

    //Integer 값 호출
    fun getInt(key: String): Int {
        val prefs =
            getPreferences()
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }

    //Long 값 호출
    fun getLong(key: String): Long {
        val prefs =
            getPreferences()
        return prefs.getLong(
            key,
            DEFAULT_VALUE_LONG
        )
    }

    //Float 값 호출
    fun getFloat(key: String): Float {
        val prefs =
            getPreferences()
        return prefs.getFloat(
            key,
            DEFAULT_VALUE_FLOAT
        )
    }

    //키 값 삭제
    fun removeKey(key: String) {
        val prefs = getPreferences()
        val edit = prefs.edit()
        edit.remove(key)
        edit.apply()
    }

    //모든 데이터 값 삭제
    fun clear() {
        val prefs = getPreferences()
        val edit = prefs.edit()
        edit.clear()
        edit.apply()
    }

    // 키쌍 리스트 구하기
    fun getAllList(): Map<String, Any>? {
        val jsonObject = JSONObject()
        val prefs = getPreferences()
        try {
            for (i in 0 until prefs.all.size) {
                jsonObject.put(prefs.all.keys.toString(), prefs.all.values)
            }
            return toMap(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    //키를 제이슨 형태로 변환
    @Throws(JSONException::class)
    fun toMap(`object`: JSONObject): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        val keysItr = `object`.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            val value = `object`[key]
            map[key] = value
        }
        return map
    }
}