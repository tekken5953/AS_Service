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

//https://velog.io/@suev72/AndroidRetrofit-Call-adapter  GSON -> kotlinx-serialization
@SuppressLint("SetTextI18n")
object HttpClient {
        lateinit var mMyAPI: MyApi  // API Interface 생성

        @Volatile   // 인스턴스가 메인 메모리를 바로 참조 -> 중복생성 방지
        private var instance: HttpClient? = null

        fun getInstance() {
            instance ?: synchronized(HttpClient::class.java) {  // 멀티스레드에서 동시생성하는 것을 막음
                instance ?: HttpClient.also {
                    instance = it
                    Log.d(TAG, "API 인스턴스 생성")
                }
            }

            // OkHttp 빌드
            val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(loggingInterceptor)

            // Create Gson Converter
            val gson = GsonBuilder().setLenient().create()

            // 서버 URL 주소에 연결, GSON Convert 활성화
            val retrofit: Retrofit by lazy {
                Retrofit.Builder()
                    .baseUrl("http://192.168.0.177:8080/api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(clientBuilder.build())
                    .build()
            }

            mMyAPI = retrofit.create(MyApi::class.java)
        }

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