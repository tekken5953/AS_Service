package app.as_service.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import app.as_service.R
import com.google.firebase.messaging.RemoteMessage


class NotificationBuilder {
    // foreground 상태에서 해드업 알림
     fun sendNotification2(context: Context, intent: Intent, data: RemoteMessage, title : String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val notificationChannelId = "500"
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationChannel = NotificationChannel(
            notificationChannelId,
            "푸시 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel description"
            enableLights(true)
            lightColor = Color.RED
            vibrationPattern = longArrayOf(0, 100, 200, 300)

            enableVibration(true)
        }

        notificationManager!!.createNotificationChannel(notificationChannel)

        val notificationBuilder = NotificationCompat.Builder(context, notificationChannelId)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.app_icon)
            .setTicker("TICKER")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(data.data.toString())
        //.setContentInfo("Info");
        notificationManager.notify( /*notification id*/1, notificationBuilder.build())
    }
}
