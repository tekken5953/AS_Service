package app.as_service.dao

object StaticDataObject {
    const val CODE_SERVER_OK: Int = 200                   // 로그인 성공
    const val CODE_SERVER_DOWN: Int = 404                 // 서버 닫힘
    const val CODE_INVALID_TOKEN: Int = 401               // 토큰 만료
    const val RESPONSE_DEFAULT: String = "NULL"           // 통신기본 값 : String(NULL)
    const val RESPONSE_FAIL: String = "FAIL"              // 통신 실패
    const val TAG_R = "Tag_Retrofit"                      // 서버통신 기본 태그 Key
    const val TAG_L = "Tag_LifeCycle"                     // 생명주기 기본 태그 Key
    const val TAG_G = "Tag_Geo"                           // 생명주기 기본 태그 Key
    const val TAG_N = "Tag_Notification"                  // FCM 기본 태그 Key
    const val TAG_D = "TAG_DrawGraph"                     // 그래프 기본 태그 Key
    const val REQUEST_LOCATION = 1                        // 위치권한 요청 Result Code
    const val REQUEST_NOTIFICATION = 2                    // 알림권한 요청 Result Code
    const val NOTIFICATION_CHANNEL_ID = "500"             //FCM 채널 ID
    const val NOTIFICATION_CHANNEL_NAME = "AIRSIGNAL"     // FCM 채널 NAME
}