package app.as_service.fcm

import android.content.Intent
import app.as_service.view.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber


class SubFCM : FirebaseMessagingService() {

    // 메시지 받았을 때
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.tag("Notification").d("FCM 메시지 수신")

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
        // 포그라운드 노티피케이션 발생
        NotificationBuilder().sendNotification(this,intent,message,"AS-Cloud FCM Test Msg", System.currentTimeMillis())
    }

    fun subTopic(s: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(s)
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Timber.tag("Notification").d(msg)
            }
    }

    fun unSubTopic(s: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(s)
            .addOnCompleteListener { task ->
                var msg = "UnSubscribed"
                if (!task.isSuccessful) {
                    msg = "UnSubscribed failed"
                }
                Timber.tag("Notification").w(msg)
            }
    }

    // 현재 토큰정보 불러오기
    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.tag("Notification").w("Fetching FCM registration token failed by $task.exception")
                return@OnCompleteListener
            }

            val token = task.result
            Timber.tag("Notification").d("FCM 토큰 : $token")
        })
    }

    // 새로운 토큰 발행
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //TODO 서버에 바뀐 토큰 보내기
        Timber.tag("Notification").d("sendRegistrationTokenToServer($token)")
    }
}