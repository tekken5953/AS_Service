package app.as_service.util

import android.content.Context
import android.os.*

class MakeVibrator(var context: Context) {
    fun run(time: Long) {
        if (Build.VERSION.SDK_INT >= 31) {
            val vibrator =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        time,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            )
        } else {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    time,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }
}