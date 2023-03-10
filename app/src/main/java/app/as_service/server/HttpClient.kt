package app.as_service.server

import android.annotation.SuppressLint
import app.as_service.dao.IgnoredKeyFile.springServerURL
import app.as_service.server.api.MyApi
import app.as_service.util.LoggerUtil
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

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
                Logger.d("API Instance 생성")
            }
        }

        // OkHttp 빌드
        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

        clientBuilder.addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                if (!message.startsWith("{") && !message.startsWith("[")) {
                    Timber.tag("Timber").d(message)
                    return
                }
                try {
                    // Timber 와 Gson setPrettyPrinting 를 이용해 json 을 보기 편하게 표시해준다.
                    LoggerUtil().logJsonTimberDebug("Timber", message)
                } catch (m: JsonSyntaxException) {
                    Timber.tag("Timber").e(message)
                }
            }

        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

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