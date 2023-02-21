package app.as_service.fcm

import android.content.Intent
import android.util.Log
import app.as_service.dao.StaticDataObject.TAG_N
import app.as_service.view.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class SubFCM : FirebaseMessagingService() {

    // 메시지 받았을 때
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG_N,"FCM 메시지 수신")

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
        // 포그라운드 노티피케이션 발생
        NotificationBuilder().sendNotification(this,intent,message,"AS-Cloud FCM Test Msg", System.currentTimeMillis())
    }

    // 현재 토큰정보 불러오기
    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG_N, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            Log.d(TAG_N, "FCM 토큰 : $token")
        })
    }

    // 새로운 토큰 발행
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //TODO 서버에 바뀐 토큰 보내기

        Log.d(TAG_N, "sendRegistrationTokenToServer($token)")
    }
}