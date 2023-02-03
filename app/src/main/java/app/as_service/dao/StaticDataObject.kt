package app.as_service.dao

import app.as_service.server.HttpClient

object StaticDataObject {
    const val CODE_SERVER_OK: Int = 200                   // 로그인 성공
    const val CODE_SERVER_DOWN: Int = 404                 // 서버 닫힘
    const val RESPONSE_DEFAULT: String = "NULL"           // 통신기본 값 : String(NULL)
    const val RESPONSE_FAIL: String = "FAIL"              // 통신실패
    const val TAG = "Tag_Retrofit"                        // 서버통신 기본 태그 Key
}