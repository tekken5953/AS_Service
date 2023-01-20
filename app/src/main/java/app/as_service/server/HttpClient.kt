package app.as_service.server

import android.annotation.SuppressLint
import android.util.Log
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.server.api.MyApi
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//https://velog.io/@suev72/AndroidRetrofit-Call-adapter
@SuppressLint("SetTextI18n")
object HttpClient {
    lateinit var mMyAPI: MyApi  // API Interface 생성

    fun getInstance() {
        Log.d(TAG, "API 인스턴스 생성")
        // OkHttp 빌드
        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)

        val gson = GsonBuilder().setLenient().create()

        // 서버 URL 주소에 연결, GSON Convert 활성화
        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("http://192.168.0.19:8080/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clientBuilder.build())
                .build()
        }

        mMyAPI = retrofit.create(MyApi::class.java)
    }

//
//    fun postSignUp(username: String, password: String, email: String, resultTv: TextView): Boolean {
//        val member = ApiModel.Member(username, password)
//        val postSignUp: Call<ApiModel.ReturnPost> = mMyAPI.(member)
//        var isSuccess = false
//        postSignUp.enqueue(object : Callback<ApiModel.ReturnPost> {
//            override fun onResponse(
//                call: Call<ApiModel.ReturnPost>,
//                response: Response<ApiModel.ReturnPost>
//            ) {
//                if (response.code() == 200) {
//                    isSuccess = true
//                    Log.d(TAG_RETROFIT, "회원가입 성공")
//                } else {
//                    isSuccess = false
//                    Log.w(TAG_RETROFIT, "Response is Success but Not Work : " + response.code())
//                }
//                resultTv.text =
//                    "Response code is ${response.code()} \nbody is ${response.body()}"
//            }
//
//            override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
//                Log.e(TAG_RETROFIT, "회원가입 실패 : ${t.printStackTrace()}")
//                isSuccess = false
//                resultTv.text = t.message.toString()
//            }
//        })
//        return isSuccess
//    }
//
//    fun postLoginUser(
//        context: Activity,
//        username: String,
//        password: String,
//        resultTv: TextView
//    ) {
//        val login = ApiModel.Login(username, password)
//        val postLogin: Call<ApiModel.AccessToken> = mMyAPI.postUsers(login)
//        postLogin.enqueue(object : Callback<ApiModel.AccessToken> {
//            override fun onResponse(
//                call: Call<ApiModel.AccessToken>,
//                response: Response<ApiModel.AccessToken>
//            ) {
//                if (response.code() == 200) {
//                    Log.d(TAG_RETROFIT, "로그인 성공 : " + response.code())
//                    val accessToken = response.body()?.access
//                    Log.d(TAG_RETROFIT, "Access Token is $accessToken")
//
//                    SharedPreferenceManager.setString(
//                        context,
//                        "accessToken",
//                        accessToken
//                    )
//
//                    SharedPreferenceManager.setString(
//                        context,
//                        "userId",
//                        getDecodeStream(accessToken, "jti")
//                    )
//
//                    val intent = Intent(context, SecondPwdActivity::class.java)
//                    context.startActivity(intent)
//                    context.finish()
//
//                } else {
//                    Log.w(TAG_RETROFIT, "Response is Success but Not Work : " + response.code())
//                }
//                resultTv.text =
//                    "Response code is ${response.code()}\nbody is ${response.message()}"
//            }
//
//            override fun onFailure(call: Call<ApiModel.AccessToken>, t: Throwable) {
//                Log.e(TAG_RETROFIT, "로그인 실패 : ${t.localizedMessage}")
//                resultTv.text = t.cause.toString()
//                context.recreate()
//            }
//        })
//    }
//
//    private fun getDecodeStream(accessToken: String?, type: String): String {
//        // jti : 이름, iat : 토큰 발행일, exp 토큰 만료일, iss alias, auth : 권한, mobile : 모바일접속여부
//        val jwtPayload = String(Base64.getUrlDecoder().decode(accessToken!!.split(".")[1]))
//        return JSONObject(jwtPayload).get(type).toString()
//    }
//
//    fun getDeviceList(token: String, resultTv: TextView) {
//        val getDeviceList: Call<List<ApiModel.GetDeviceList>> = mMyAPI.getDeviceList(token)
//        getDeviceList.enqueue(object : Callback<List<ApiModel.GetDeviceList>> {
//            override fun onResponse(
//                call: Call<List<ApiModel.GetDeviceList>>,
//                response: Response<List<ApiModel.GetDeviceList>>
//            ) {
//                if (response.code() == 200) {
//                    var count = 0
//                    val mList: List<ApiModel.GetDeviceList>? = response.body()
//                    mList?.forEach { _ ->
//                        resultTv.text =
//                            resultTv.text.toString() + "$count -> [\n${mList}\n]\n"
//                        count++
//                    }
//                } else {
//                    Log.w(TAG_RETROFIT, "Response is Success but Not Work : " + response.code())
//                    resultTv.text =
//                        "Response code is  ${response.code()} \nbody is ${response.body()}"
//                }
//
//            }
//
//            override fun onFailure(call: Call<List<ApiModel.GetDeviceList>>, t: Throwable) {
//                resultTv.text = t.printStackTrace().toString()
//            }
//        })
//    }
//
//    fun postDevice(token: String, resultTv: TextView, item: ApiModel.AddDevice) {
//        val postAddDevice: Call<ApiModel.ReturnPost> = mMyAPI.postAddDevice(token, item)
//        postAddDevice.enqueue(object : Callback<ApiModel.ReturnPost> {
//            override fun onResponse(
//                call: Call<ApiModel.ReturnPost>,
//                response: Response<ApiModel.ReturnPost>
//            ) {
//                if (response.code() == 200) {
//                    resultTv.text = "통신 성공 : ${response.body()?.result}"
//                } else {
//                    Log.w(TAG_RETROFIT, "Response is Success but Not Work : " + response.code())
//                    resultTv.text =
//                        "Response code is  ${response.code()} \nbody is ${response.body()}"
//                }
//            }
//
//            override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
//                resultTv.text = t.message
//            }
//        })
//    }
//
//    fun getMyInfo(token: String, resultTv: TextView) {
//        val getMyInfo: Call<ApiModel.GetMyInfo> = mMyAPI.getMyInfo(token)
//        getMyInfo.enqueue(object : Callback<ApiModel.GetMyInfo> {
//            override fun onResponse(
//                call: Call<ApiModel.GetMyInfo>,
//                response: Response<ApiModel.GetMyInfo>
//            ) {
//                if (response.code() == 200) {
//                    resultTv.text = "통신 성공\n${response.body().toString()}"
//                } else {
//                    Log.w(TAG_RETROFIT, "Response is Success but Not Work : " + response.code())
//                    resultTv.text =
//                        "Response code is  ${response.code()} \nbody is ${response.body()}"
//                }
//            }
//
//            override fun onFailure(call: Call<ApiModel.GetMyInfo>, t: Throwable) {
//                resultTv.text = t.message
//            }
//        })
//    }
//
//    fun putMyEmail(token: String, newEmail: String, resultTv: TextView) {
//        val putMyPassword: Call<ApiModel.ReturnPost> =
//            mMyAPI.putMyEmail(token, ApiModel.PutMyEmail(newEmail))
//        putMyPassword.enqueue(object : Callback<ApiModel.ReturnPost> {
//            override fun onResponse(
//                call: Call<ApiModel.ReturnPost>,
//                response: Response<ApiModel.ReturnPost>
//            ) {
//                if (response.code() == 200) {
//                    resultTv.text = "변경 성공\n${response.body().toString()}"
//                } else {
//                    Log.w(TAG_RETROFIT, "Response is Success but Not Work : " + response.code())
//                    resultTv.text =
//                        "Response code is  ${response.code()} \nbody is ${response.body()}"
//                }
//            }
//
//            override fun onFailure(call: Call<ApiModel.ReturnPost>, t: Throwable) {
//                resultTv.text = t.message
//            }
//        })
//    }
//
//    fun getData(token: String, resultTv: TextView) {
//        val getDeviceList: Call<List<ApiModel.GetDeviceList>> = mMyAPI.getDeviceList(token)
//        getDeviceList.enqueue(object : Callback<List<ApiModel.GetDeviceList>> {
//            override fun onResponse(
//                call: Call<List<ApiModel.GetDeviceList>>,
//                responseDevice: Response<List<ApiModel.GetDeviceList>>
//            ) {
//                val mList: List<ApiModel.GetDeviceList>? = responseDevice.body()
//                for (i: Int in 0 until responseDevice.body()!!.size) {
//                    val getData: Call<ApiModel.GetData> =
//                        mMyAPI.getData(responseDevice.body()!![i].device, "query", token)
//                    getData.enqueue(object : Callback<ApiModel.GetData> {
//                        override fun onResponse(
//                            call: Call<ApiModel.GetData>,
//                            responseData: Response<ApiModel.GetData>
//                        ) {
//                            if (responseData.code() == 200) {
//                                resultTv.text =
//                                    "${mList?.get(i)!!.device} [\n${responseData.body().toString()}\n]\n"
//                            } else {
//                                Log.w(
//                                    TAG_RETROFIT,
//                                    "Response is Success but Not Work : " + responseData.code()
//                                )
//                                resultTv.text =
//                                    "Response code is  ${responseData.code()} \nbody is ${responseData.body()}"
//                            }
//                        }
//
//                        override fun onFailure(call: Call<ApiModel.GetData>, t: Throwable) {
//                            resultTv.text = t.message
//                        }
//                    })
//                }
//            }
//
//            override fun onFailure(call: Call<List<ApiModel.GetDeviceList>>, t: Throwable) {
//                resultTv.text = t.message
//            }
//        })
//
//    }
}