package app.as_service.repository

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.CODE_SERVER_DOWN
import app.as_service.dao.StaticDataObject.CODE_SERVER_OK
import app.as_service.dao.StaticDataObject.TAG_R
import com.orhanobut.logger.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("PropertyName")
class SignUpRepo : BaseRepository() {
    var _signUpResultData = MutableLiveData<String>()    // 회원가입 Response Body : String(Ok Sign)

    fun loadSignUpResult(username: String, phone: String, password: String) {
        val signUpModel = ApiModel.Member(username, phone, password)

        httpClient.mMyAPI.postSignUp(signUpModel).run {
            enqueue(object : Callback<ApiModel.ReturnPost> {
                override fun onResponse(
                    call: Call<ApiModel.ReturnPost>,
                    response: Response<ApiModel.ReturnPost>
                ) {
                    when (response.code()) {
                        CODE_SERVER_OK -> {
                            Logger.d(TAG_R, "회원가입 통신 성공")
                            _signUpResultData.value = CODE_SERVER_OK.toString()
                        }
                        CODE_SERVER_DOWN -> {
                            Logger.e(TAG_R, "서버 연결 불가 : ${response.code()}")
                        }
                        else -> {
                            Logger.w(TAG_R, "통신 성공 but 예상치 못한 에러 발생: ${response.code()}")
                        }
                    }
                }

                override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
                    Logger.e(TAG_R, "로그인 실패 : ${t.localizedMessage}")
                }
            })
        }
    }
}