package aynicode.ec.servicios.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Esteban on 3/29/2018.
 */

public class RestartLocationServiceReceiver extends BroadcastReceiver {

    private static final String TAG = "RestartLocationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Restarting service");
        Intent serviceIntent = new Intent(context, CheckDeviceStatusService.class);
        context.startService(serviceIntent);
    }
}
