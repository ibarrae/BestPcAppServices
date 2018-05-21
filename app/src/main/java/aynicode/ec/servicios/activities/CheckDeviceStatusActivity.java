package aynicode.ec.servicios.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import aynicode.ec.servicios.R;
import aynicode.ec.servicios.services.CheckDeviceStatusService;
import es.dmoral.toasty.Toasty;

public class CheckDeviceStatusActivity extends AppCompatActivity {

    public static final int ASK_COARSE_LOCATION_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askforPermissions();
        }
        setContentView(R.layout.activity_main);
        Intent deviceStatusIntent = new Intent(this, CheckDeviceStatusService.class);
        startService(deviceStatusIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_COARSE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    askforPermissions();
                }
        }
    }

    private void askforPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ASK_COARSE_LOCATION_PERMISSION);
    }
}