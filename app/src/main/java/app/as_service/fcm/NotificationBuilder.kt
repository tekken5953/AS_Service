package app.as_service.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import app.as_service.R
import app.as_service.view.login.LoginActivity
import com.google.firebase.messaging.RemoteMessage


class NotificationBuilder(context: Context, data: RemoteMessage,title : String) : BroadcastReceiver() {
//    var INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED
    private val mData = data
    private val mContext = context
    private var mTitle = title

    fun getNotification() {
        val intent = Intent(mContext, NotificationBuilder::class.java)
        onReceive(mContext, intent)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intentActivity = Intent(context, LoginActivity::class.java)
        intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intentActivity,
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, "default")
        builder.setContentText(mData.notification!!.body)
        builder.setSmallIcon(R.drawable.app_icon)
        val large = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.star_fill)
        builder.setLargeIcon(large)
        builder.setContentTitle(mTitle)
        builder.setWhen(System.currentTimeMillis())
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
        val ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
            context,
            RingtoneManager.TYPE_NOTIFICATION
        )
        builder.setSound(ringtoneUri)
        val vibrate = longArrayOf(0, 100, 200, 300)
        builder.setVibrate(vibrate)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "default", "Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        notificationManager.notify(1, builder.build())
    }
}