package app.as_service.util;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE;

import android.app.Activity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackBarUtils {
    Snackbar snackbar;
    public void makeSnack(View view, Activity activity, String msg) {
        Runnable r = () -> {
            snackbar = Snackbar.make(view,msg,Snackbar.LENGTH_SHORT);
            snackbar.setDuration(500);
            snackbar.setAnimationMode(ANIMATION_MODE_SLIDE);
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
            snackbar.show();
        };
        activity.runOnUiThread(r);
    }
}
