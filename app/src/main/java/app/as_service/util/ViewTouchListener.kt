package app.as_service.util

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint("ClickableViewAccessibility")
class ViewTouchListener {
    //Image View
    fun onPressView(view: View) {
        view.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        view.scaleX = 0.97f
                        view.scaleY = 0.97f
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        view.scaleX = 1f
                        view.scaleY = 1f
                        return true
                    }
                }
                return false
            }
        })
    }
}