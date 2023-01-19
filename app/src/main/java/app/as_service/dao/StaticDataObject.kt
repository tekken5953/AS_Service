package app.as_service.dao

import app.as_service.repository.LoginRepo
import app.as_service.repository.SignUpRepo
import app.as_service.server.HttpClient

object StaticDataObject {
    const val CODE_SERVER_OK: Int = 200                   // 로그인 성공
    const val CODE_SERVER_DOWN: Int = 404                 // 서버 닫힘
    const val RESPONSE_DEFAULT: String = "NULL"           // 통신실패 시 기본 값 : String(NULL)
    const val TAG = "Tag_Retrofit"                        // 서버통신 기본 태그 Key
    val httpClient = HttpClient                           // 서버 클라이언트 객체
    val signUpRepository = SignUpRepo()                   // 회원가입 리포지터리
}