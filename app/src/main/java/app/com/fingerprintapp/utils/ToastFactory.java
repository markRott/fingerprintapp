package app.com.fingerprintapp.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import app.com.fingerprintapp.BuildConfig;

public class ToastFactory {

    public static void showDebugToast(Context context, String message) {
        if (BuildConfig.DEBUG) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(Context context, int message) {
        Toast toast = Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
