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
import app.as_service.dao.StaticDataObject.NOTIFICATION_CHANNEL_ID
import app.as_service.dao.StaticDataObject.NOTIFICATION_CHANNEL_NAME
import com.google.firebase.messaging.RemoteMessage


class NotificationBuilder {
    // foreground 상태에서 해드업 알림
     fun sendNotification(context: Context, intent: Intent, data: RemoteMessage, title : String, time: Long) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel description"
            enableLights(true)
            lightColor = Color.RED
            vibrationPattern = longArrayOf(0, 100, 200, 300)

            enableVibration(true)
        }

        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(time)
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(data.data.toString())
        notificationManager!!.run {
            createNotificationChannel(notificationChannel)
            notify(1, notificationBuilder.build())
        }
    }
}
