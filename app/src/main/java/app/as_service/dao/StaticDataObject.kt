package app.as_service.dao

object StaticDataObject {
    const val CODE_SERVER_OK: Int = 200                   // 로그인 성공
    const val CODE_SERVER_DOWN: Int = 404                 // 서버 닫힘
    const val RESPONSE_DEFAULT: String = "NULL"           // 통신기본 값 : String(NULL)
    const val RESPONSE_FAIL: String = "FAIL"              // 통신실패
    const val TAG_R = "Tag_Retrofit"                      // 서버통신 기본 태그 Key
    const val TAG_L = "Tag_LifeCycle"                     // 생명주기 기본 태그 Key
    const val TAG_G = "Tag_Geo"                     // 생명주기 기본 태그 Key
    const val REQUEST_LOCATION = 1                        // 위치권한요청 Result Code
    const val REQUEST_NOTIFICATION = 2                    // 알림권한요청 Result Code
}