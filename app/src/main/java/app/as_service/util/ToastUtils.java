package app.as_service.util;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtils {
    Activity mContext;
    Toast toast;

    public ToastUtils(Activity context) {
        this.mContext = context;
    }

    public void shortMessage(final String message) {
        Runnable r = () -> {
            cancelToast();
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.show();
        };
        mContext.runOnUiThread(r);
    }

    public void customDurationMessage(final String message, final int duration) {
        Runnable r = () -> {
            cancelToast();
            toast = Toast.makeText(mContext, message, duration);
            toast.show();
        };
        mContext.runOnUiThread(r);
    }

    private void cancelToast() {
        if (toast != null) {
            this.mContext.runOnUiThread(() -> toast.cancel());
        }
        toast = new Toast(mContext);
    }
}
