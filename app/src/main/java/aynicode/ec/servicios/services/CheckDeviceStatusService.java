package aynicode.ec.servicios.services;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.widget.Toast;

import com.permissioneverywhere.PermissionEverywhere;
import com.permissioneverywhere.PermissionResponse;
import com.permissioneverywhere.PermissionResultCallback;

import aynicode.ec.servicios.R;
import aynicode.ec.servicios.activities.CheckDeviceStatusActivity;
import aynicode.ec.servicios.presenter.CheckDeviceStatusPresenter;
import aynicode.ec.servicios.presenter.CheckDeviceStatusPresenterInt;
import aynicode.ec.servicios.view.CheckDeviceStatusView;
import es.dmoral.toasty.Toasty;

/**
 * Created by Esteban on 3/29/2018.
 */

public class CheckDeviceStatusService extends Service implements CheckDeviceStatusView {

    private AlertDialog activateGpsDialog;
    private final CheckDeviceStatusPresenterInt checkDeviceStatusPresenterInt = new CheckDeviceStatusPresenter(this, this);
    private Context context;
    private String servicesError;
    private String servicesSuccess;
    private String permissionTitle;
    private String permissionMessage;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initAtributes();
        initServicesNotEnabledDialog();
        startBackgroundTask();
        startForegroundService();
        return START_STICKY;
    }

    private void initAtributes() {
        this.context = getApplicationContext();
        this.servicesError = getString(R.string.services_error);
        this.servicesSuccess = getString(R.string.services_success);
        this.permissionTitle = getString(R.string.notification_title);
        this.permissionMessage = getString(R.string.permission_not_granted);
    }

    private void startBackgroundTask() {
        checkDeviceStatusPresenterInt.checkDeviceStatusContinously();
    }

    private void startForegroundService() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "1")
                .setContentTitle(getApplicationContext().getString(R.string.notification_title))
                .setSmallIcon(R.drawable.service_icon);
        startForeground(101,
                notificationBuilder.build());
    }

    private void initServicesNotEnabledDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustomStyle));
        alertDialogBuilder.setTitle(R.string.reminder);
        alertDialogBuilder.setMessage(R.string.activate_gps_message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNeutralButton(R.string.close_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkDeviceStatusPresenterInt.checkDeviceStatusManually();
            }
        });
        activateGpsDialog = alertDialogBuilder.create();
        activateGpsDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("RestartLocationService"));
    }

    @Override
    public void showStatusDialog() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!activateGpsDialog.isShowing()) {
                    activateGpsDialog.show();
                }
            }
        });
    }

    @Override
    public void hideStatusDialog() {
        if (activateGpsDialog.isShowing()) {
            activateGpsDialog.dismiss();
        }
    }

    @Override
    public void showServiceEnabledToast() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toasty.info(context, servicesSuccess, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showServiceErrorToast() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toasty.error(context, servicesError, Toast.LENGTH_SHORT).show();
            }
        });
        showStatusDialog();
    }

    @Override
    public void askPermissions() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PermissionEverywhere.getPermission(getApplicationContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, CheckDeviceStatusActivity.ASK_COARSE_LOCATION_PERMISSION, permissionTitle, permissionMessage, R.mipmap.ic_launcher_round).enqueue(new PermissionResultCallback() {
                    @Override
                    public void onComplete(PermissionResponse permissionResponse) {
                        if (!permissionResponse.isGranted()) {
                            askPermissions();
                        }
                    }
                });
            }
        });
    }
}
