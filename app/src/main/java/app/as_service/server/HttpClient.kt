package app.as_service.server

import android.annotation.SuppressLint
import android.util.Log
import app.as_service.dao.IgnoredKeyFile.springServerURL
import app.as_service.dao.StaticDataObject.TAG_R
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
//                    Log.d(TAG_R, "API 인스턴스 생성")
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
                    .baseUrl(springServerURL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(clientBuilder.build())
                    .build()
            }

            // API 인터페이스 형태로 레트로핏 클라이언트 생성
            mMyAPI = retrofit.create(MyApi::class.java)
        }
}